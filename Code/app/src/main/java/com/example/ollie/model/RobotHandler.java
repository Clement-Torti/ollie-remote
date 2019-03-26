package com.example.ollie.model;

import android.Manifest;
import android.content.Context;
import android.support.v4.content.PermissionChecker;

import com.example.ollie.BaseActivity;
import com.orbotix.ConvenienceRobot;
import com.orbotix.common.DiscoveryAgent;
import com.orbotix.common.DiscoveryAgentEventListener;
import com.orbotix.common.DiscoveryException;
import com.orbotix.common.Robot;
import com.orbotix.common.RobotChangedStateListener;
import com.orbotix.le.DiscoveryAgentLE;

import java.util.List;

public class RobotHandler {

    private static ConvenienceRobot robot = null;


    public static void setRobot(ConvenienceRobot _robot) {
        robot = _robot;
    }

    public static ConvenienceRobot getRobot() {
        return robot;
    }

    public static void deleteRobot() {
        robot = null;
    }



}
