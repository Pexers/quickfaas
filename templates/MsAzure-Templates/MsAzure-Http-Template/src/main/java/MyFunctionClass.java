/*
 * Copyright (c) 4/30/2022, Pexers (https://github.com/Pexers)
 */

import quickfaas.triggers.http.HttpRequestQf;
import quickfaas.triggers.http.HttpResponseQf;

public class MyFunctionClass {

    public void myFunction(HttpRequestQf req, HttpResponseQf res) {
        res.send(200, "Hello World!");
    }

}