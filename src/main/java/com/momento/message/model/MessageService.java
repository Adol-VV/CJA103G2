package com.momento.message.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageService {

	@Autowired
	private MessageRepository repository;

	public MessageVO addMessage(MessageVO messageVO) {
		return repository.save(messageVO);
	}

	public MessageVO updateMessage(MessageVO messageVO) {
		return repository.save(messageVO);
	}

	public void deleteMessage(Integer messageId) {
		if (repository.existsById(messageId)) {
			repository.deleteById(messageId);
		}
	}

	public MessageVO getOneMessage(Integer messageId) {
		Optional<MessageVO> optional = repository.findById(messageId);
		return optional.orElse(null);
	}

	public List<MessageVO> getAll() {
		return repository.findAll();
	}

	public List<MessageVO> getMessagesByArticleId(Integer articleId) {
		return repository.findByArticleVO_ArticleId(articleId);
	}

	public List<MessageVO> getMessagesByStatus(Integer status) {
		return repository.findByStatus(status);
	}
}
