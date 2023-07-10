/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.decisionmaker.deciders.searchbackpressure;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.performanceanalyzer.AppContext;
import org.opensearch.performanceanalyzer.decisionmaker.deciders.Decider;
import org.opensearch.performanceanalyzer.decisionmaker.deciders.Decision;
import org.opensearch.performanceanalyzer.rca.framework.core.RcaConf;
import org.opensearch.performanceanalyzer.rca.store.rca.searchbackpressure.SearchBackPressureClusterRCA;

/** decider to change the dynamic settings of SearchBackPressure In-flight Cancellation */
public class SearchBackPressureDecider extends Decider {
    private static final Logger LOG = LogManager.getLogger(SearchBackPressureDecider.class);
    public static final String NAME = "SearchBackPressureDecider";

    /* TO ADD: SearchBackPressureDecider should have SeachBackPressurePolicy able to evaluate the search back pressure */

    private int currentIteration = 0;
    private SearchBackPressureClusterRCA searchBackPressureClusterRCA;

    public SearchBackPressureDecider(
            long evalIntervalSeconds,
            int decisionFrequency,
            SearchBackPressureClusterRCA searchBackPressureClusterRCA) {
        super(evalIntervalSeconds, decisionFrequency);
        this.searchBackPressureClusterRCA = searchBackPressureClusterRCA;
        LOG.info("SearchBackPressureDecider created");
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public Decision operate() {
        LOG.info("SearchBackPressureDecider operate() with currentIteration: {}", currentIteration);

        Decision decision = new Decision(System.currentTimeMillis(), NAME);
        currentIteration += 1;
        if (currentIteration < decisionFrequency) {
            return decision;
        }

        currentIteration = 0;

        // SearchBackPressure Policy is always accepted
        // List<Action> searchBackPressureActions = seart.evaluate();
        // oldGenPolicyActions.forEach(decision::addAction);
        return decision;
    }

    /* Read RCA Config to fill the dynamic threshold settings for the SearchBackPressure Service */
    @Override
    public void readRcaConf(RcaConf conf) {
        super.readRcaConf(conf);
        // oldGenDecisionPolicy.setRcaConf(conf);
    }

    /* Set AppContext for SearchBackPressurePolicy */
    @Override
    public void setAppContext(final AppContext appContext) {
        super.setAppContext(appContext);
        // oldGenDecisionPolicy.setAppContext(appContext);
    }
}
