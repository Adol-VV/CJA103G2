package com.momento.messagereport.model;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageReportService {

	@Autowired
	private MessageReportRepository repository;

	public MessageReportVO addMessageReport(MessageReportVO messageReportVO) {
		return repository.save(messageReportVO);
	}

	public MessageReportVO updateMessageReport(MessageReportVO messageReportVO) {
		return repository.save(messageReportVO);
	}

	public void deleteMessageReport(Integer messageReportId) {
		if (repository.existsById(messageReportId)) {
			repository.deleteById(messageReportId);
		}
	}

	public MessageReportVO getOneMessageReport(Integer messageReportId) {
		Optional<MessageReportVO> optional = repository.findById(messageReportId);
		return optional.orElse(null);
	}

	public List<MessageReportVO> getAll() {
		return repository.findAll();
	}
}
