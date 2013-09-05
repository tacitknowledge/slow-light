package com.tacitknowledge.slowlight.proxy.stubs;

import java.io.FileNotFoundException;
import java.net.ConnectException;

/**
* Created by IntelliJ IDEA.
* User: witherspore
* Date: 9/3/13
* Time: 11:43 AM
* To change this template use File | Settings | File Templates.
*/
public class StubbedServiceErrorImpl implements StubbedService {
    public Integer callService() throws ConnectException, FileNotFoundException {
        throw new ConnectException();
    }

    public Integer callOtherService() throws ConnectException, FileNotFoundException {
        return 0;
    }
}
