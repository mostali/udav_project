package mpc.map;

import mpc.env.APP;
import mpc.env.Env;
import mpc.exception.WhatIsTypeException;
import mpc.fs.UF;
import mpc.fs.UFS;
import mpc.fs.fd.RES;
import mpc.types.ruprops.RuProps;
import mpc.types.ruprops.URuProps;
import mpu.X;
import mpu.core.ARR;
import mpu.pare.Pare;

import java.nio.file.Path;
import java.util.Map;

//application.properties location
public enum APType {
    RUN_OPTS, //cmd args
    SYS,
    ENV,
    LOCAL_MAP, // custom local map
    FILE_OPTS,
    FILE_MAP,
    APPDIR, // appVol dir
    RL, //ap from run dir
    RSRC //  package resources
    ;

    Void initBootMap_AP(Map<Pare<APType, String>, Map<String, String>> bootMap, String filename, boolean required) {
        switch (this) {
            case SYS:
                bootMap.put(Pare.of(this, filename), (Map) System.getProperties());
                return null;
            case ENV:
                bootMap.put(Pare.of(this, filename), System.getenv());
                return null;
            case APPDIR: {
                Path appDataDir = APP.LOCATION.getAppDataDirOrNull();
                if (appDataDir == null) {
                    return !required ? null : X.throwIllegalStateException("Not found appDir");
                }
                return loadMapFromDir(bootMap, appDataDir, filename, required);
            }
            case RL:
                return loadMapFromDir(bootMap, Env.RUN_LOCATION, filename, required);
            case RSRC:
                String cnt = RES.of(Env.class, "/" + UF.normFileStart(filename)).cat(null);
                if (cnt != null) {
                    Map rsrcMap = URuProps.getRuPropertiesClassic(ARR.as(cnt.split("\\n")));
                    bootMap.put(Pare.of(this, filename), rsrcMap);
                    return null;
                }
                return throwRequired(filename, required);
            default:
                throw new WhatIsTypeException(this);
        }
    }

    private Void loadMapFromDir(Map<Pare<APType, String>, Map<String, String>> bootMap, Path fromDir, String filename, boolean required) {
        Path fileMap = fromDir.resolve(filename);
        if (UFS.existFile(fileMap)) {
            bootMap.put(Pare.of(this, filename), RuProps.of(fileMap).toMap());
            return null;
        }
        return throwRequired(filename, required);
    }

    private Void throwRequired(String filename, boolean required) {
        return !required ? null : X.throwIllegalStateException("Boot Resource '%s' not exists from %s", filename, this);
    }

}
