package com.hkt.ruby.fuse.demo.utils;

import java.io.Serializable;

public interface IResultCode extends Serializable {
    String getMessage();

    int getCode();
}
