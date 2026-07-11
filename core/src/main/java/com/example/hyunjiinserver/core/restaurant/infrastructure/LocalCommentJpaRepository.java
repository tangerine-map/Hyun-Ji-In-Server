package com.example.hyunjiinserver.core.restaurant.infrastructure;

import com.example.hyunjiinserver.core.restaurant.domain.LocalComment;
import org.springframework.data.jpa.repository.JpaRepository;

interface LocalCommentJpaRepository extends JpaRepository<LocalComment, Long> {
}
