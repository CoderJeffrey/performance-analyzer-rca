/*
 * Copyright OpenSearch Contributors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.opensearch.performanceanalyzer;


import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.opensearch.performanceanalyzer.commons.metrics.AllMetrics;
import org.opensearch.performanceanalyzer.rca.framework.util.InstanceDetails;
import org.opensearch.performanceanalyzer.rca.store.collector.NodeConfigCache;
import org.opensearch.performanceanalyzer.reader.ClusterDetailsEventProcessor;

/**
 * The PA agent process is composed of multiple components. The PA Reader and RCA are two such
 * components that are independent in a way they process information but also share some information
 * such as the node and the cluster details. Today, some of these information is accessed by calling
 * static methods and members. This is a bad idea. This class encapsulates such information and is
 * created right at the start in the {@code PerformanceAnalyzerApp}.
 */
public class AppContext {
    private volatile ClusterDetailsEventProcessor clusterDetailsEventProcessor;
    // initiate a node config cache within each AppContext space
    // to store node config settings from OpenSearch
    private final NodeConfigCache nodeConfigCache;
    private volatile Set<String> mutedActions;

    public AppContext() {
        this.clusterDetailsEventProcessor = null;
        this.nodeConfigCache = new NodeConfigCache();
        this.mutedActions = ImmutableSet.of();
    }

    public AppContext(AppContext other) {
        this.clusterDetailsEventProcessor =
                new ClusterDetailsEventProcessor(other.clusterDetailsEventProcessor);

        // Initializing this as we don't want to copy the entire cache.
        this.nodeConfigCache = new NodeConfigCache();
        this.mutedActions = ImmutableSet.copyOf(other.getMutedActions());
    }

    public void setClusterDetailsEventProcessor(
            final ClusterDetailsEventProcessor clusterDetailsEventProcessor) {
        this.clusterDetailsEventProcessor = clusterDetailsEventProcessor;
    }

    public InstanceDetails getMyInstanceDetails() {
        InstanceDetails ret = new InstanceDetails(AllMetrics.NodeRole.UNKNOWN);

        if (clusterDetailsEventProcessor != null
                && clusterDetailsEventProcessor.getCurrentNodeDetails() != null) {
            ClusterDetailsEventProcessor.NodeDetails nodeDetails =
                    clusterDetailsEventProcessor.getCurrentNodeDetails();
            ret = new InstanceDetails(nodeDetails);
        }
        return ret;
    }

    /**
     * Can be used to get all the nodes in the cluster.
     *
     * @return Returns an empty list of the details are not available or else it provides the
     *     immutable list of nodes in the cluster.
     */
    public List<InstanceDetails> getAllClusterInstances() {
        List<InstanceDetails> ret = Collections.EMPTY_LIST;

        if (clusterDetailsEventProcessor != null) {
            ret = getInstanceDetailsFromNodeDetails(clusterDetailsEventProcessor.getNodesDetails());
        }
        return ret;
    }

    public List<InstanceDetails> getDataNodeInstances() {
        List<InstanceDetails> ret = Collections.EMPTY_LIST;
        if (clusterDetailsEventProcessor != null) {
            ret =
                    getInstanceDetailsFromNodeDetails(
                            clusterDetailsEventProcessor.getDataNodesDetails());
        }
        return ret;
    }

    private static List<InstanceDetails> getInstanceDetailsFromNodeDetails(
            final List<ClusterDetailsEventProcessor.NodeDetails> nodeDetails) {
        ImmutableList.Builder<InstanceDetails> instanceDetails = ImmutableList.builder();
        for (ClusterDetailsEventProcessor.NodeDetails node : nodeDetails) {
            instanceDetails.add(new InstanceDetails(node));
        }
        return instanceDetails.build();
    }

    @VisibleForTesting
    public ClusterDetailsEventProcessor getClusterDetailsEventProcessor() {
        return clusterDetailsEventProcessor;
    }

    public Set<InstanceDetails> getPeerInstances() {
        return ImmutableSet.copyOf(
                getAllClusterInstances().stream()
                        .skip(1) // Skipping the first instance as it is self.
                        .collect(Collectors.toSet()));
    }

    public NodeConfigCache getNodeConfigCache() {
        return this.nodeConfigCache;
    }

    public InstanceDetails getInstanceById(InstanceDetails.Id instanceIdKey) {
        return getPeerInstances().stream()
                .filter(x -> x.getInstanceId().equals(instanceIdKey))
                .findFirst()
                .orElse(new InstanceDetails(AllMetrics.NodeRole.UNKNOWN));
    }

    public boolean isActionMuted(final String action) {
        return this.mutedActions.contains(action);
    }

    public void updateMutedActions(final Set<String> mutedActions) {
        this.mutedActions = ImmutableSet.copyOf(mutedActions);
    }

    public Set<String> getMutedActions() {
        return this.mutedActions;
    }
}
