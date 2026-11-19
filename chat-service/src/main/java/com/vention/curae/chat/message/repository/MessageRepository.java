package com.vention.curae.chat.message.repository;

import com.vention.curae.chat.message.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Pageable;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	Page<Message> findByChatId(Long chatId, Pageable pageable);

}
