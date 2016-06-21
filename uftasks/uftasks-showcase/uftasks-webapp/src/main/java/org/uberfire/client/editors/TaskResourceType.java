package org.uberfire.client.editors;

import javax.enterprise.context.ApplicationScoped;

import org.uberfire.backend.vfs.Path;
import org.uberfire.client.workbench.type.ClientResourceType;

import com.google.gwt.user.client.ui.IsWidget;

@ApplicationScoped
public class TaskResourceType implements ClientResourceType {

    @Override
    public String getShortName() {
        return "task";
    }

    @Override
    public String getDescription() {
        return "TO-DO Task file";
    }

    @Override
    public String getPrefix() {
        return "";
    }

    @Override
    public String getSuffix() {
        return "task";
    }

    @Override
    public int getPriority() {
        return 0;
    }

    @Override
    public String getSimpleWildcardPattern() {
        return "*.task";
    }

    @Override
    public boolean accept(Path path) {
        return path.getFileName().endsWith( "." + getSuffix() );
    }

    @Override
    public IsWidget getIcon() {
        return null;
    }

}
