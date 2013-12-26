package com.tacitknowledge.slowlight.proxyserver.systest.util.client;

import java.util.ArrayList;
import java.util.List;

/**
* @author Alexandr Donciu (adonciu@tacitknowledge.com)
*/
public class ServerResponse
{
    private int size;
    private List<byte[]> bytesList = new ArrayList<byte[]>();

    public List<byte[]> get()
    {
        return new ArrayList<byte[]>(bytesList);
    }

    public void set(final byte[] bytes)
    {
        size += bytes.length;
        bytesList.add(bytes);
    }

    public void reset()
    {
        size = 0;
        bytesList.clear();
    }

    public int size()
    {
        return size;
    }
}
