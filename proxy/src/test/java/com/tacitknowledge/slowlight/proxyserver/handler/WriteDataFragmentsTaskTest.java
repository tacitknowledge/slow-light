package com.tacitknowledge.slowlight.proxyserver.handler;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import io.netty.util.concurrent.EventExecutor;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * @author Alexandr Donciu (adonciu@tacitknowledge.com)
 */
@RunWith(MockitoJUnitRunner.class)
public class WriteDataFragmentsTaskTest
{
    @Mock
    private Channel channel;

    @Mock
    private ChannelHandlerContext ctx;

    @Mock
    private ChannelPromise promise;

    @Mock
    private ChannelFuture future;

    @Mock
    private EventExecutor executor;

    @Test
    public void taskShouldDoNothingIfNoReadableBytes() throws Exception
    {
        final ByteBuf msg = mock(ByteBuf.class);
        doReturn(0).when(msg).readableBytes();

        final WriteDataFragmentsTask writeDataFragmentsTask = new WriteDataFragmentsTask(ctx, msg, promise, 3, 0);
        writeDataFragmentsTask.run();

        verifyNoMoreInteractions(ctx);
    }

    @Test
    public void taskShouldWriteMessageInFragments() throws Exception
    {
        final ByteBuf msg = Unpooled.wrappedBuffer("test message".getBytes());

        doReturn(future).when(ctx).writeAndFlush(any());

        final WriteDataFragmentsTask writeDataFragmentsTask = new WriteDataFragmentsTask(ctx, msg, promise, 3, 0);
        writeDataFragmentsTask.run();

        final ArgumentCaptor<ByteBuf> msgFragmentCaptor = ArgumentCaptor.forClass(ByteBuf.class);
        verify(ctx).writeAndFlush(msgFragmentCaptor.capture());

        final ArgumentCaptor<ChannelFutureListener> futureListenerCaptor = ArgumentCaptor.forClass(ChannelFutureListener.class);
        verify(future).addListener(futureListenerCaptor.capture());

        final ByteBuf msgFragment = msgFragmentCaptor.getValue();
        assertThat(msgFragment.readableBytes(), is(equalTo(3)));
    }

    @Test
    public void taskShouldWriteOnlyReadableBytesWhenDataSizeIsGreater() throws Exception
    {
        final ByteBuf msg = Unpooled.wrappedBuffer("test".getBytes());

        doReturn(future).when(ctx).writeAndFlush(any());

        final WriteDataFragmentsTask writeDataFragmentsTask = new WriteDataFragmentsTask(ctx, msg, promise, 10, 0);
        writeDataFragmentsTask.run();

        final ArgumentCaptor<ByteBuf> msgFragmentCaptor = ArgumentCaptor.forClass(ByteBuf.class);
        verify(ctx).writeAndFlush(msgFragmentCaptor.capture());

        final ByteBuf msgFragment = msgFragmentCaptor.getValue();
        assertThat(msgFragment.readableBytes(), is(equalTo(4)));
    }

    @Test
    public void taskShouldWriteAllReadableBytesWhenDataSizeSetToZero() throws Exception
    {
        final ByteBuf msg = Unpooled.wrappedBuffer("test message".getBytes());

        doReturn(future).when(ctx).writeAndFlush(any());

        final WriteDataFragmentsTask writeDataFragmentsTask = new WriteDataFragmentsTask(ctx, msg, promise, 0, 0);
        writeDataFragmentsTask.run();

        final ArgumentCaptor<ByteBuf> msgFragmentCaptor = ArgumentCaptor.forClass(ByteBuf.class);
        verify(ctx).writeAndFlush(msgFragmentCaptor.capture());

        final ByteBuf msgFragment = msgFragmentCaptor.getValue();
        assertThat(msgFragment.readableBytes(), is(equalTo(12)));
    }

    @Test
    public void listenerShouldScheduleNextMessageFragmentIfAvailable() throws Exception
    {
        final ByteBuf msg = mock(ByteBuf.class);

        final WriteDataFragmentsTask writeDataFragmentsTask = new WriteDataFragmentsTask(ctx, msg, promise, 3, 100);
        final WriteDataFragmentsTask.WriteDataListener writeDataListener = writeDataFragmentsTask.new WriteDataListener();

        doReturn(true).when(future).isSuccess();
        doReturn(10).when(msg).readableBytes();
        doReturn(executor).when(ctx).executor();

        writeDataListener.operationComplete(future);

        verify(executor).schedule(writeDataFragmentsTask, 100, TimeUnit.MILLISECONDS);
    }

    @Test
    public void listenerShouldChannelPromiseToSuccessIfNoMoreDataToRead() throws Exception
    {
        final ByteBuf msg = mock(ByteBuf.class);

        final WriteDataFragmentsTask writeDataFragmentsTask = new WriteDataFragmentsTask(ctx, msg, promise, 3, 100);
        final WriteDataFragmentsTask.WriteDataListener writeDataListener = writeDataFragmentsTask.new WriteDataListener();

        doReturn(true).when(future).isSuccess();
        doReturn(0).when(msg).readableBytes();
        doReturn(executor).when(ctx).executor();

        writeDataListener.operationComplete(future);

        verify(promise).setSuccess();
    }

    @Test
    public void listenerShouldCloseChannelIfWriteFutureCompletedUnsuccessfully() throws Exception
    {
        final ByteBuf msg = mock(ByteBuf.class);

        final WriteDataFragmentsTask writeDataFragmentsTask = new WriteDataFragmentsTask(ctx, msg, promise, 3, 100);
        final WriteDataFragmentsTask.WriteDataListener writeDataListener = writeDataFragmentsTask.new WriteDataListener();

        doReturn(false).when(future).isSuccess();
        doReturn(channel).when(future).channel();

        writeDataListener.operationComplete(future);

        verify(channel).close();
    }
}
