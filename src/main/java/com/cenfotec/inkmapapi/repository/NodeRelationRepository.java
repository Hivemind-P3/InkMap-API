package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.Node;
import com.cenfotec.inkmapapi.models.NodeMap;
import com.cenfotec.inkmapapi.models.NodeRelation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NodeRelationRepository extends JpaRepository<NodeRelation, Long> {

    /**
     * All relations for a given node map.
     */
    List<NodeRelation> findByNodeMapOrderByIdAsc(NodeMap nodeMap);

    /**
     * Finds a relation by ID only if it belongs to the given node map.
     */
    Optional<NodeRelation> findByIdAndNodeMap(Long id, NodeMap nodeMap);

    /**
     * Checks whether a directed relation from source to target already exists.
     */
    boolean existsBySourceNodeAndTargetNode(Node sourceNode, Node targetNode);

    /**
     * Deletes all relations where the given node appears as source or target.
     * Called before deleting a node to avoid FK constraint violations.
     */
    void deleteBySourceNodeOrTargetNode(Node sourceNode, Node targetNode);
}
