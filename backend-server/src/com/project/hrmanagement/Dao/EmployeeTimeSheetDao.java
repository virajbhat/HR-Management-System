package com.project.hrmanagement.Dao;

import java.util.List;

import javax.transaction.Transactional;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.project.hrmanagement.model.TimeSheet;

@Repository
public class EmployeeTimeSheetDao implements IEmployeeTimeSheetDao {

	private SessionFactory sessionFactory;

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	@Transactional
	public TimeSheet addTimeSheet(TimeSheet timeSheet) {

		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery("Select empId,taskDate from TimeSheet where empId=:e and taskDate=:td");

		query.setParameter("e", timeSheet.getEmpId());
		query.setParameter("td", timeSheet.getTaskDate());

		@SuppressWarnings("unchecked")
		List<String> existCheck = query.list();
		if (existCheck.isEmpty()) {

			// filled state set to 1 and timesheet added
			timeSheet.setIsFilled(1);
			session.save(timeSheet);

			// timesheet returned to UI
			return timeSheet;
		} else {

			// timeSheet already exists please update
			return null;
		}
	}

	@Override
	@Transactional
	public Integer editTimeSheet(TimeSheet timeSheet) {

		Session session = sessionFactory.getCurrentSession();

		Query query = session.createQuery(
				"Select empId,taskDate,isApproved,isFilled from TimeSheet where empId=:e and taskDate=:td");

		query.setParameter("e", timeSheet.getEmpId());
		query.setParameter("td", timeSheet.getTaskDate());

		@SuppressWarnings("unchecked")
		List<Object[]> timeSheetOfEmp = (List<Object[]>) query.list();
		System.out.println(timeSheetOfEmp);
		for (Object[] tse : timeSheetOfEmp) {
			// check for approved flag
			Integer isApproved = (Integer) tse[2];

			// check for already filled flag
			Integer isFilled = (Integer) tse[3];

			if (isApproved == 1) {

				// time sheet already approved cannot update
				return 0;
			} else {
				// timesheet not filled cannot update please use addTask
				if (isFilled == 0) {
					return 0;
				}
			}
		}
		{

			Query hql = session.createQuery(
					"Update TimeSheet set taskName = :tn, swipeIn = :si, swipeOut = :so where empId = :ep and taskDate = :td");
			hql.setParameter("ep", timeSheet.getEmpId());
			hql.setParameter("td", timeSheet.getTaskDate());
			hql.setParameter("tn", timeSheet.getTaskName());
			hql.setParameter("si", timeSheet.getSwipeIn());
			hql.setParameter("so", timeSheet.getSwipeOut());

			int empIdList1 = hql.executeUpdate();
			System.out.println(empIdList1);

			// time sheet updated successfully
			return 1;

		}

	}

	@Override
	@Transactional
	public Integer updateTimeSheetStatus(TimeSheet timeSheet) {
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createQuery(
				"Select empId,taskDate,isApproved,isFilled from TimeSheet where empId=:e and taskDate=:td");
		query.setParameter("e", timeSheet.getEmpId());
		query.setParameter("td", timeSheet.getTaskDate());

		@SuppressWarnings("unchecked")
		List<Object[]> approvalCheck = (List<Object[]>) query.list();
		System.out.println(approvalCheck);
		for (Object[] tse : approvalCheck) {
			// check for approved flag
		
			Integer isApproved = (Integer) tse[2];

			// check for already filled flag
			Integer isFilled = (Integer) tse[3];
			if(isApproved == null)
			{
				//time sheet is in rejected status and now sent to approval procedure
				break;
			}else
			{

			if (isApproved == 1) {
				// time sheet already approved
				return 1;
			}
			if (isFilled == 0)

				// timesheet not filled so cannot approve
				return 0;
		}
		}

		{
			Query hql = session
					.createQuery("Update TimeSheet set isApproved = :ia where empId = :ep and taskDate = :td");

			// setting isApproved flag to 1
			hql.setParameter("ia", 1);
			hql.setParameter("td", timeSheet.getTaskDate());
			hql.setParameter("ep", timeSheet.getEmpId());

			// rows affected
			int rowsAffected = hql.executeUpdate();

			// just testing
			System.out.println(rowsAffected);

			// timeSheet approved successfully
			return 1;

		}

	}

	@Override
	@Transactional
	public Integer rejectTimeSheet(TimeSheet timeSheet) {
		Session session = sessionFactory.getCurrentSession();
		
		
		Query hql = session.createQuery(
				"Update TimeSheet set isApproved = :ia where empId = :ep and taskDate = :td");
		hql.setParameter("ep", timeSheet.getEmpId());
		hql.setParameter("td", timeSheet.getTaskDate());
		
		//setting isApproved flag to null
		hql.setParameter("ia", null);

		int empIdList1 = hql.executeUpdate();
		System.out.println(empIdList1);

		//timeSheet added to rejected status
		return 1;
	}

}
