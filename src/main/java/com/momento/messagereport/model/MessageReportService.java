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

	public List<MessageReportVO> getReportsByStatus(Integer status) {
		return repository.findByStatus(status);
	}

	public List<MessageReportVO> searchReports(String type, String keyword, String sort) {
		org.springframework.data.domain.Sort sortObj = org.springframework.data.domain.Sort.by(
				"oldest".equals(sort) ? org.springframework.data.domain.Sort.Direction.ASC
						: org.springframework.data.domain.Sort.Direction.DESC,
				"reportedAt");

		String searchKey = (keyword == null) ? "" : keyword;

		if ("pending".equals(type)) {
			// Pending comments (status = 0)
			if (searchKey.isEmpty()) {
				// Reuse findByStatus but we need sorting. Repository findByStatus doesn't have
				// sort param yet
				// but JPA handles method naming sort. Actually let's just use the query method
				// with empty string matches all like %""%?
				// LIKE '%%' works.
				return repository.findByStatusAndKeyword(0, "", sortObj);
			} else {
				return repository.findByStatusAndKeyword(0, searchKey, sortObj);
			}
		} else {
			// All comments (Reported tab shows all in current implementation)
			if (searchKey.isEmpty()) {
				return repository.findAll(sortObj);
			} else {
				return repository.findByKeyword(searchKey, sortObj);
			}
		}
	}
}
