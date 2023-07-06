/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer.decisionmaker.deciders.policy;


import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.opensearch.performanceanalyzer.decisionmaker.actions.Action;
import org.opensearch.performanceanalyzer.decisionmaker.deciders.DecisionPolicy;
import org.opensearch.performanceanalyzer.rca.store.rca.searchbackpressure.SearchBackPressureClusterRCA;

/*
 * Decides if the SearchBackPressure Service related thresholds (e.g. search_backpressure.search_task_heap_threshold) should be autotuned
 * suggestions actions to autotune the thresholds
 */
public class SearchBackPressurePolicy implements DecisionPolicy {
    private static final Logger LOG = LogManager.getLogger(SearchBackPressurePolicy.class);
    private SearchBackPressureClusterRCA searchBackPressureClusterRCA;

    private AppContext appContext;
    private RcaConf rcaConf;

    /* constructor */
    public SearchBackPressurePolicy(SearchBackPressureClusterRCA searchBackPressureClusterRCA) {
        this.searchBackPressureClusterRCA = searchBackPressureClusterRCA;
    }

    /*
     * emit actions for autotune
     * potential actions: SearchbpActionIncreaseHeapThreshold, SearchbpActionDereaseHeapThreshold
     * @return List<Action>
     */
    @Override
    public List<Action> evaluate() {
        return null;
    }
}
