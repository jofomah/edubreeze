package com.edubreeze.service;

import com.github.sarxos.webcam.Webcam;

import java.util.List;

public class WebCamService {

    public static List<Webcam> getWebcams()
    {
        return Webcam.getWebcams();
    }
}
