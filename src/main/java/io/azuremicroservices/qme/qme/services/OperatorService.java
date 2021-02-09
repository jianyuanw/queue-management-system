package io.azuremicroservices.qme.qme.services;

import io.azuremicroservices.qme.qme.models.Operator;
import io.azuremicroservices.qme.qme.repositories.OperatorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OperatorService {

	private final OperatorRepository operatorRepo;

	@Autowired
	public OperatorService(OperatorRepository operatorRepo) {
		this.operatorRepo = operatorRepo;
	}

	public List<Operator> findAllOperators() {
		return operatorRepo.findAll();
	}

	public void createOperator(Operator operator) {
		operatorRepo.save(operator);
	}
	
	public void updateOperator(Operator operator) {
		operatorRepo.save(operator);
	}	

	public Optional<Operator> findOperatorById(Long operatorId) {
		return operatorRepo.findById(operatorId);
	}

	public List<Operator> findAllOperatorsByBranchId(Long branchId) {
		return operatorRepo.findAllByBranch_Id(branchId);
	}
	
	public boolean operatorNameExistsForBranch(String operatorName, Long branchId) {
		return operatorRepo.findAllByBranch_IdAndName(branchId, operatorName).size() > 0;
	}

	@Transactional
	public void deleteOperator(Operator operator) {
		operatorRepo.delete(operator);
	}	
	
}
