package com.example.polybooster.data.repository

import com.example.polybooster.data.dao.CardDao
import com.example.polybooster.data.model.Card

class CardRepository(private val cardDao: CardDao) {

    suspend fun getAllCards(): List<Card> {
        return cardDao.getAllCards()
    }

    suspend fun insertCard(card: Card) {
        cardDao.insertCard(card)
    }

    suspend fun insertAll(cards: List<Card>) {
        cardDao.insertAll(cards)
    }

    suspend fun deleteCard(card: Card) {
        cardDao.deleteCard(card)
    }

    suspend fun getRandomLockedCards(limit: Int): List<Card> {
        return cardDao.getRandomLockedCards(limit)
    }

    suspend fun unlockCard(cardId: Int) {
        cardDao.unlockCard(cardId)
    }
}
