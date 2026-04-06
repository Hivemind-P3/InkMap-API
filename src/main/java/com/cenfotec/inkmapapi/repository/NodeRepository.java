package com.cenfotec.inkmapapi.repository;

import com.cenfotec.inkmapapi.models.Node;
import com.cenfotec.inkmapapi.models.NodeMap;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NodeRepository extends JpaRepository<Node, Long> {

    /**
     * All nodes for a node map, ordered by ID ascending.
     */
    Page<Node> findByNodeMapOrderByIdAsc(NodeMap nodeMap, Pageable pageable);

    /**
     * Finds a node by ID only if it belongs to the given node map.
     */
    Optional<Node> findByIdAndNodeMap(Long id, NodeMap nodeMap);
}
