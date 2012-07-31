package com.android.test.scirocco2.util;

public class CommandUtil {
    
    public enum Command {
        CMD_INIT, CMD_FW_APP, CMD_INIT_WEB, CMD_FW_WEB, CMD_INSTALL, 
        CMD_VERSION, CMD_DEVICE_MODEL, CMD_INST_FB2, CMD_INIT_FB2, CMD_TAKE_FB2, CMD_COPY_FB2
    }
    
    public static String getCommand(Command cmd, String[] param) {
        StringBuilder command = new StringBuilder();
        switch (cmd) {
            case CMD_INIT:
                command.append("-s ");
                command.append(param[0]);
                command.append(" shell am instrument ");
                command.append(param[1]);
                command.append("/com.google.android.testing.nativedriver.server.ServerInstrumentation");
            break;
            case CMD_FW_APP:
                command.append("-s ");
                command.append(param[0]);
                command.append(" forward tcp:54129 tcp:54129");
            break;
            case CMD_INIT_WEB:
                command.append("-s ");
                command.append(param[0]);
                command.append(" shell am start -a android.intent.action.MAIN -n org.openqa.selenium.android.app/.MainActivity");
            break;
            case CMD_FW_WEB:
                command.append("-s ");
                command.append(param[0]);
                command.append(" forward tcp:8080 tcp:8080");
            break;
            case CMD_INSTALL:
                command.append("-s ");
                command.append(param[0]);
                command.append(" install -r ");
                command.append(param[1]);
            break;
            case CMD_VERSION:
                command.append("-s ");
                command.append(param[0]);
                command.append(" shell getprop ro.build.version.release");
            break;
            case CMD_DEVICE_MODEL:
                command.append("-s ");
                command.append(param[0]);
                command.append(" shell getprop ro.product.model");
            break;
            case CMD_INST_FB2:
                command.append("-s ");
                command.append(param[0]);
                command.append(" push ");
                command.append(param[1]);
                command.append(" /data/local/");
            break;
            case CMD_INIT_FB2:
                command.append("-s ");
                command.append(param[0]);
                command.append(" shell chmod 755 /data/local/fb2png");
            break;
            case CMD_TAKE_FB2:
                command.append("-s ");
                command.append(param[0]);
                command.append(" shell /data/local/fb2png /data/local/tmp.png");
            break;
            case CMD_COPY_FB2:
                command.append("-s ");
                command.append(param[0]);
                command.append(" pull /data/local/tmp.png ");
                command.append(param[1]);
            break;
            default:
                // no action
            break;
        }
        return command.toString();
    }
}
