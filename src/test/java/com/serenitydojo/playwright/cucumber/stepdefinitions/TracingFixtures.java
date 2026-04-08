package com.serenitydojo.playwright.cucumber.stepdefinitions;

import com.microsoft.playwright.Tracing;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;

import java.nio.file.Paths;

public class TracingFixtures {

    @Before
    void beforeEach() {

        CucumberFixtures.getContext().tracing().start(
                new Tracing.StartOptions()
                        .setScreenshots(true)
                        .setSnapshots(true)
                        .setSources(true)
        );
    }

    @After
    void afterEach(Scenario scenario) {

        String traceTargetName = "target/trace/trace_"
                + scenario.getName().replace(" ", "_") + ".zip";

        CucumberFixtures.getContext().tracing().stop(
                new Tracing.StopOptions()
                        .setPath(Paths.get(traceTargetName))
        );
    }
}
