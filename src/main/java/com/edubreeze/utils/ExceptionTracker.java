package com.edubreeze.utils;

import io.sentry.Sentry;

public class ExceptionTracker {
    public static void track(Throwable ex) {
        Sentry.capture(ex);
    }
}
