package com.starmcc.beanfun.entity.model;

import lombok.Data;

@Data
public class Version {

    private int major;
    private int minor;
    private int patch;

    public Version(String versionString) {
        versionString = versionString.replace("v", "");
        String[] parts = versionString.split("\\.");
        if (parts.length >= 1) {
            this.major = Integer.parseInt(parts[0]);
        }
        if (parts.length >= 2) {
            this.minor = Integer.parseInt(parts[1]);
        }
        if (parts.length >= 3) {
            this.patch = Integer.parseInt(parts[2]);
        }
    }

    public int compareTo(Version other) {
        if (this.major != other.major) {
            return this.major - other.major;
        }
        if (this.minor != other.minor) {
            return this.minor - other.minor;
        }
        return this.patch - other.patch;
    }

    @Override
    public String toString() {
        return "v" + major + "." + minor + "." + patch;
    }
}