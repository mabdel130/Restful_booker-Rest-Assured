package com.Restful_booker.api.logging;

import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Core;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.Property;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;

/**
 * Log4j2 appender that mirrors every log event into {@link LogCollector} so the
 * lines can be attached to the Allure report per scenario — the same output you
 * see on the console during the run.
 * <p>
 * Referenced from {@code log4j2.xml} as {@code <ScenarioLog name="..."/>}.
 */
@Plugin(name = "ScenarioLog", category = Core.CATEGORY_NAME,
        elementType = Appender.ELEMENT_TYPE, printObject = true)
public final class ScenarioLogAppender extends AbstractAppender {

    private ScenarioLogAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout, true, Property.EMPTY_ARRAY);
    }

    @Override
    public void append(LogEvent event) {
        LogCollector.append(new String(getLayout().toByteArray(event), StandardCharsets.UTF_8));
    }

    @PluginFactory
    public static ScenarioLogAppender createAppender(
            @PluginAttribute("name") String name,
            @PluginElement("Layout") Layout<? extends Serializable> layout,
            @PluginElement("Filter") Filter filter) {
        return new ScenarioLogAppender(name,
                filter,
                layout != null ? layout : PatternLayout.createDefaultLayout());
    }
}
