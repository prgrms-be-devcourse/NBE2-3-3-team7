package com.project.popupmarket.repository

import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import com.project.popupmarket.entity.Receipts
import com.project.popupmarket.entity.User
import com.project.popupmarket.enums.ReservationStatus
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Repository

@Repository
class AdminRepository (
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext,
) {
    fun findReceiptsByFilter(
        status: ReservationStatus?,
        sorting: String?,
        pageable: Pageable,
    ): Page<Receipts> {
        val query = entityManager.createQuery(
            jpql {
                select(entity(Receipts::class))
                    .from(entity(Receipts::class))
                    .where(
                        path(Receipts::reservationStatus).isNull()
                            .or(path(Receipts::reservationStatus).eq(status))
                    )
                    .orderBy(path(Receipts::reservedAt).desc())
            }, context
        )

        query.firstResult = pageable.offset.toInt()
        query.maxResults = pageable.pageSize

        val receipts = query.resultList

        val totalCount:Long = entityManager.createQuery(
            jpql {
                select(count(entity(Receipts::class)))
                    .from(entity(Receipts::class))
                    .where(
                        path(Receipts::reservationStatus).isNull()
                            .or(path(Receipts::reservationStatus).eq(status))
                    )
            }, context
        ).singleResult.toLong()

        return PageImpl(receipts, pageable, totalCount)
    }

    // TODO : User 관련 Kotlin Migration 이후에 수정
    fun findUserInfoById(id: Long): User {
        TODO("User Entity Required Kotlin Migration")
    }
}