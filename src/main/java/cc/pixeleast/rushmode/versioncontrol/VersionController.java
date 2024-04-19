package cc.pixeleast.rushmode.versioncontrol;

import java.io.IOException;
import java.util.Properties;

public class VersionController {
    private final String commit;
    private final String commitAbbrev;
    private final String commitDescribe;
    private final String buildTime;
    private final Boolean isDirty;

    public VersionController() {
        Properties properties = new Properties();
        try {
            properties.load(getClass().getClassLoader().getResourceAsStream("git.properties"));
            commit = properties.getProperty("git.commit.id");
            commitAbbrev = properties.getProperty("git.commit.id.abbrev");
            commitDescribe = properties.getProperty("git.commit.id.describe");
            buildTime = properties.getProperty("git.build.time");
            isDirty = Boolean.parseBoolean(properties.getProperty("git.dirty"));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }
    }

    public String getCommit() {
        return commit;
    }

    public String getCommitAbbrev() {
        return commitAbbrev;
    }

    public String getCommitDescribe() {
        return commitDescribe;
    }

    public String getBuildTime() {
        return buildTime;
    }

    public Boolean isDirty() {
        return isDirty;
    }
}
