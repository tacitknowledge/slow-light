package com.tacitknowledge.perf.degradation.proxy.stubs;

import java.io.FileNotFoundException;
import java.net.ConnectException;

/**
* Created by IntelliJ IDEA.
* User: mshort
* Date: 9/3/13
* Time: 11:43 AM
* To change this template use File | Settings | File Templates.
*/
public class StubbedServiceImpl implements StubbedService{
    public Integer callService() throws ConnectException, FileNotFoundException {
        return 0;
    }

    public Integer callOtherService() throws ConnectException, FileNotFoundException {
        return 0;
    }
}
