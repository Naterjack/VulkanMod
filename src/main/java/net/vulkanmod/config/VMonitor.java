package net.vulkanmod.config;

import com.mojang.blaze3d.systems.RenderSystem;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.PointerBuffer;

public class VMonitor {
    private static VMonitor[] monitors;
    private static int monitorCount = 1;
    private VideoResolution[] videoResolutions;
    int index;
    private long monitorPointer = 0;

    public VMonitor(long monitorPointer, int index, VideoResolution[] videoResolutions){
        this.monitorPointer = monitorPointer;
        this.index = index;
        this.videoResolutions = videoResolutions;
    }

    public long getPointer(){
        return this.monitorPointer;
    }

    public int GetIndex(){
        return this.index;
    }

    public VideoResolution[] getVideoResolutions(){
        return this.videoResolutions;
    }

    public static void init() {
        RenderSystem.assertOnRenderThread();
        GLFW.glfwInit();
        monitors = populateMonitors();
    }

    public static String toString(Integer index) {
        if (index>=monitorCount){
            return "Unknown";
        }
        VMonitor targetMonitor = monitors[index];
        long pointer = targetMonitor.getPointer();

        if (pointer != 0){
            return Integer.toString(index) + " : " + GLFW.glfwGetMonitorName(pointer);
        }else{
            return Integer.toString(index);
        }
        }
    
    public static Integer[] getMonitorIndicies(){
        Integer[] returnArr = new Integer[monitorCount];
        for (int i=0; i<monitorCount; i++){
            returnArr[i] = Integer.valueOf(i);
        }
        return returnArr;
    }

    public static VMonitor getMonitorsByIndex(int index) {
        if (index>=monitorCount){
            return monitors[0];
        }else {
            return monitors[index];
        }
    }

    public static VMonitor[] getMonitors() {
        return monitors;
    }

    public static int getNumberConnectedMonitors(){
        return monitorCount;
    }

    public static VMonitor getFirstAvailable() {
        if(monitors != null){
            return monitors[0];
        } 
        else {
            long monitorID = -1; //GLFW.glfwGetPrimaryMonitor();
            VideoResolution[] dummyResArr = new VideoResolution[1];
            dummyResArr[0] = new VideoResolution(-1, -1);
            return new VMonitor(
                monitorID,
                0,
                dummyResArr
            );
        }
    }

    public static VMonitor[] populateMonitors(){
        VMonitor[] monitorArray;
        PointerBuffer monitorBuffer = GLFW.glfwGetMonitors();
        if (monitorBuffer!=null){
            monitorCount = monitorBuffer.limit();
            monitorArray = new VMonitor[monitorCount];
            for (int i=0; i<monitorCount; i++){
                long monitorID = monitorBuffer.get();
                monitorArray[i]=new VMonitor(
                    monitorID,
                    i,
                    VideoResolution.populateVideoResolutions(monitorID).clone()
                );
            }
        }else{
            monitorArray = new VMonitor[1];
            long monitorID = GLFW.glfwGetPrimaryMonitor();
            monitorArray[0]=new VMonitor(
                    monitorID,
                    0,
                    VideoResolution.populateVideoResolutions(monitorID).clone()
                );
        }
        return monitorArray;
    }
 
    //TODO getters for monitor/defaultmonitor
}
