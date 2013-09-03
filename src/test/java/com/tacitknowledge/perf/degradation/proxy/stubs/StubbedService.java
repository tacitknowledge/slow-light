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
public interface StubbedService {
    Integer callService() throws ConnectException, FileNotFoundException;
    Integer callOtherService() throws ConnectException, FileNotFoundException;


}
