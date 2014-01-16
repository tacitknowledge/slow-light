package com.tacitknowledge.slowlight.proxyserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
public class RandomDataChannelHandlerTest extends BaseChannelHandlerTest
{
    @Test
    public void handlerShouldRespondWithRandomData() throws Exception
    {
        final int dataFragments = 100;
        final int dataFragmentSize = 3;

        doReturn(Integer.toString(dataFragments)).when(handlerConfig).getParam(RandomDataChannelHandler.PARAM_DATA_FRAGMENTS);
        doReturn(Integer.toString(dataFragmentSize)).when(handlerConfig).getParam(RandomDataChannelHandler.PARAM_DATA_FRAGMENT_SIZE);

        final RandomDataChannelHandler randomDataChannelHandler = new RandomDataChannelHandler(handlerConfig);

        randomDataChannelHandler.channelRead(channelHandlerContext, msg);

        verify(eventExecutor).schedule((Runnable) Matchers.anyObject(), eq(0L), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    public void handlerShouldRequestChannelReadOnChannelActivation() throws Exception
    {
        final RandomDataChannelHandler randomDataChannelHandler = new RandomDataChannelHandler(handlerConfig);
        randomDataChannelHandler.channelActive(channelHandlerContext);

        verify(channelHandlerContext).read();
    }

    @Test
    public void taskShouldGenerateRandomDataWithSpecifiedDataFragmentSize()
    {
        final RandomDataChannelHandler randomDataChannelHandler = new RandomDataChannelHandler(handlerConfig);
        final RandomDataChannelHandler.GenerateRandomDataTask generateRandomDataTask = randomDataChannelHandler
                .new GenerateRandomDataTask(channelHandlerContext, 3, 10);

        final ChannelFuture writeFuture = mock(ChannelFuture.class);
        doReturn(writeFuture).when(channel).writeAndFlush(any());

        generateRandomDataTask.run();

        final ArgumentCaptor<ByteBuf> msgCaptor = ArgumentCaptor.forClass(ByteBuf.class);
        verify(channel).writeAndFlush(msgCaptor.capture());
        verify(writeFuture).addListener(Matchers.<GenericFutureListener<Future<? super Void>>>any());

        final ByteBuf msg = msgCaptor.getValue();
        assertThat(msg.readableBytes(), is(equalTo(10)));
    }

    @Test
    public void listenerShouldScheduleNextRandomDataFragmentGenerateTask() throws Exception
    {
        final RandomDataChannelHandler randomDataChannelHandler = new RandomDataChannelHandler(handlerConfig);
        final RandomDataChannelHandler.GenerateRandomDataTask generateRandomDataTask = randomDataChannelHandler
                .new GenerateRandomDataTask(channelHandlerContext, 3, 10);
        final RandomDataChannelHandler.GenerateRandomDataTask.GenerateDataListener generateDataListener = generateRandomDataTask
                .new GenerateDataListener();

        doReturn(true).when(channelFuture).isSuccess();
        doReturn(eventExecutor).when(channelHandlerContext).executor();

        generateDataListener.operationComplete(channelFuture);

        verify(eventExecutor).schedule(generateRandomDataTask, 0, TimeUnit.MILLISECONDS);
    }

    @Test
    public void listenerShouldRequestNextChannelReadIfNoMoreDataFragmentsToBeGenerated() throws Exception
    {
        final RandomDataChannelHandler randomDataChannelHandler = new RandomDataChannelHandler(handlerConfig);
        final RandomDataChannelHandler.GenerateRandomDataTask generateRandomDataTask = randomDataChannelHandler
                .new GenerateRandomDataTask(channelHandlerContext, 0, 10);
        final RandomDataChannelHandler.GenerateRandomDataTask.GenerateDataListener generateDataListener = generateRandomDataTask
                .new GenerateDataListener();

        final ChannelFuture writeFuture = mock(ChannelFuture.class);
        doReturn(true).when(writeFuture).isSuccess();

        generateDataListener.operationComplete(writeFuture);

        verify(channel).read();
    }

    @Test
    public void listenerShouldCloseChannelIfItCompletedUnsuccessfully() throws Exception
    {
        final RandomDataChannelHandler randomDataChannelHandler = new RandomDataChannelHandler(handlerConfig);
        final RandomDataChannelHandler.GenerateRandomDataTask generateRandomDataTask = randomDataChannelHandler
                .new GenerateRandomDataTask(channelHandlerContext, 0, 10);
        final RandomDataChannelHandler.GenerateRandomDataTask.GenerateDataListener generateDataListener = generateRandomDataTask
                .new GenerateDataListener();

        doReturn(false).when(channelFuture).isSuccess();

        final Channel futureChannel = mock(Channel.class);
        doReturn(futureChannel).when(channelFuture).channel();

        generateDataListener.operationComplete(channelFuture);

        verify(futureChannel).close();
    }
}
