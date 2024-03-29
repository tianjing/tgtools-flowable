package com.github.tianjing.tgtools.flowable.autoconfigure.service;

import com.github.tianjing.tgtools.flowable.autoconfigure.entity.FlowElementExtCollection;
import com.github.tianjing.tgtools.flowable.autoconfigure.util.BpmnModelParser;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.*;
import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.RepositoryServiceImpl;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tgtools.exceptions.APPErrorException;
import tgtools.util.StringUtil;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * @author 田径
 * @create 2019-08-17 22:37
 * @desc
 **/
@Service
public class FlowService {

    @Autowired
    ModelService modelService;
    @Autowired
    RepositoryService repositoryService;

    /**
     * 发布流程
     *
     * @param pModelId
     * @return
     * @throws APPErrorException
     */
    public Deployment deloy(String pModelId) throws APPErrorException {
        Model modelData = modelService.getModel(pModelId);

        byte[] bytes = modelService.getBpmnXML(modelData);
        if (bytes == null) {
            throw new APPErrorException("模型数据为空，请先设计流程并成功保存，再进行发布。");
        }

        BpmnModel model = modelService.getBpmnModel(modelData);
        if (model.getProcesses().size() == 0) {
            throw new APPErrorException("数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        String processName = modelData.getName() + ".bpmn20.xml";
        return repositoryService.createDeployment()
                .name(modelData.getName())
                .addBytes(processName, bpmnBytes)
                .deploy();
    }

    /**
     * 启动流程
     *
     * @param pFlowId
     * @param pBussionId
     * @param pVar
     */
    public void startFlow(String pFlowId, String pBussionId, HashMap pVar) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRuntimeService().startProcessInstanceById(pFlowId, pBussionId, pVar);
    }


    /**
     * 根据流程ID获取流程模型
     * 获取流程模型后 可以 通过 BpmnModelParser 解析模型
     *
     * @param pFlowID
     * @return
     */
    public BpmnModel getBpmn(String pFlowID) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        return processEngine.getRepositoryService().getBpmnModel(pFlowID);
    }

    /**
     * 根据TaskID获取流程模型
     * 获取流程模型后 可以 通过 BpmnModelParser 解析模型
     *
     * @param pTaskID
     * @return
     */
    public BpmnModel getBpmnByTaskID(String pTaskID) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String flowid = processEngine.getHistoryService().createHistoricTaskInstanceQuery().taskId(pTaskID).singleResult().getProcessDefinitionId();
        return processEngine.getRepositoryService().getBpmnModel(flowid);
    }

    /**
     * 根据业务ID获取流程对象
     * 获取流程模型后 可以 通过 BpmnModelParser 解析模型
     *
     * @param BusinessKey
     * @return
     */
    public BpmnModel getBpmnByBusinessKey(String BusinessKey) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        String flowid = processEngine.getHistoryService().createHistoricTaskInstanceQuery().processInstanceBusinessKey(BusinessKey).singleResult().getProcessDefinitionId();
        return processEngine.getRepositoryService().getBpmnModel(flowid);
    }

    /**
     * 根据流程ID和业务ID启动流程
     *
     * @param pFlowID      流程ID
     * @param pBusinessKey 业务ID
     */
    public void startFlowBykey(String pFlowID, String pBusinessKey) {
        startFlowBykey(pFlowID, pBusinessKey, new HashMap<String, Object>());
    }

    /**
     * 根据流程ID和业务ID启动流程
     *
     * @param pFlowID      流程ID
     * @param pBusinessKey 业务ID
     * @param pVariables   变量
     */
    public void startFlowBykey(String pFlowID, String pBusinessKey, Map<String, Object> pVariables) {
        ProcessEngine processEngine = ProcessEngines.getDefaultProcessEngine();
        processEngine.getRuntimeService().startProcessInstanceById(pFlowID, pBusinessKey, pVariables);

    }

    /**
     * 根据业务ID 获取 当前流程实例中中第一个Task
     * 如果不存在则返回null
     *
     * @param pBusinessKey
     * @return
     * @throws APPErrorException
     */
    public Task getTaskWhenFirstUserTask(String pBusinessKey) throws APPErrorException {
        List<Task> tasks = getTasks(pBusinessKey);
        if (tasks.size() < 1) {
            throw new APPErrorException("业务ID:" + pBusinessKey + "  找不到TASK");
        }
        BpmnModelParser paser = BpmnModelParser.getInstance(getBpmnByTaskID(tasks.get(0).getId()));
        for (int i = 0, count = tasks.size(); i < count; i++) {
            if (paser.isFirstUserTask(tasks.get(i).getTaskDefinitionKey())) {
                return tasks.get(i);
            }
        }
        return null;
    }

    /**
     * 是否含有第一个UserTask
     * 可以用于判断 当前流程实例中 是否经过第一个任务
     *
     * @param pBusinessKey
     * @return
     * @throws APPErrorException
     */
    public boolean isFirstUserTask(String pBusinessKey) throws APPErrorException {
        return null != getTaskWhenFirstUserTask(pBusinessKey);
    }

    /**
     * 获取所有历史Task
     * 根据创建时间正序排列
     *
     * @param pBusinessKey
     * @return
     */
    public List<HistoricTaskInstance> getHistoricTask(String pBusinessKey) {
        return ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricTaskInstanceQuery().processInstanceBusinessKey(pBusinessKey).orderByTaskCreateTime().asc().list();
    }

    /**
     * 根据TaskID 获取当前任务信息
     *
     * @param pTaskID
     * @return
     */
    public HistoricTaskInstance getHistoricTaskByTaskID(String pTaskID) {
        return ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricTaskInstanceQuery().taskId(pTaskID).singleResult();
    }

    /**
     * 根据业务ID和任务定义 在历史任务中查询任务信息
     *
     * @param pBusinessKey
     * @param pTaskDefID
     * @return
     */
    public HistoricTaskInstance getHistoricTask(String pBusinessKey, String pTaskDefID) {
        List<HistoricTaskInstance> list = getHistoricTask(pBusinessKey);
        if (null != list && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                HistoricTaskInstance task = list.get(i);
                if (task.getTaskDefinitionKey().equals(pTaskDefID)) {
                    return task;
                }
            }
        }
        return null;
    }

    /**
     * 流程挂起
     * 挂起的流程将不能再继续下去
     *
     * @param processDefinitionId
     */
    public void suspensionFlow(String processDefinitionId) {
        ProcessEngines.getDefaultProcessEngine().getRepositoryService().suspendProcessDefinitionById(processDefinitionId);
    }

    /**
     * 流程激活
     *
     * @param processDefinitionId
     */
    public void activateFlow(String processDefinitionId) {
        ProcessEngines.getDefaultProcessEngine().getRepositoryService().activateProcessDefinitionById(processDefinitionId);
    }

    /**
     * 签收任务
     * 签收任务意味着任务的 Assignee 字段 会设置成输入的userID
     *
     * @param pTaskID
     * @param pUserID
     * @throws APPErrorException
     */
    public void claimTask(String pTaskID, String pUserID) throws APPErrorException {
        if (StringUtil.isNullOrEmpty(pTaskID)) {
            throw new APPErrorException("无效任务ID");
        }
        if (StringUtil.isNullOrEmpty(pUserID)) {
            throw new APPErrorException("无效用户ID");
        }
        Task task = ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().taskId(pTaskID).singleResult();

        if (!StringUtil.isNullOrEmpty(task.getAssignee())) {
            if (!task.getAssignee().equals(pUserID)) {
                throw new APPErrorException("当前任务已被签收,请勿重复操作。");
            }
        } else {
            ProcessEngines.getDefaultProcessEngine().getTaskService().claim(pTaskID, pUserID);
        }
    }

    /**
     * 启动流程
     * 启动流程时会同时增加一条业务数据
     *
     * @param pBusinessKey 业务主键
     * @param pFlowId      流程ID
     * @param pUserID      用户ID
     * @return 返回业务主键
     * @throws APPErrorException
     */
    public String[] startFlow(String pBusinessKey, String pFlowId, String pUserID) throws APPErrorException {
        return startFlow(pBusinessKey, pFlowId, pUserID, new HashMap<String, Object>());
    }

    /**
     * 启动流程
     * 启动流程时会同时增加一条业务数据
     *
     * @param pBusinessKey 业务主键
     * @param pFlowid      流程ID
     * @param pUserID      用户ID
     * @param useClaimTask 是否签收
     * @return 返回业务主键
     * @throws APPErrorException
     */
    public String[] startFlow(String pBusinessKey, String pFlowid, String pUserID, boolean useClaimTask) throws APPErrorException {
        return startFlow(pBusinessKey, pFlowid, pUserID, new HashMap<String, Object>(), useClaimTask);
    }

    /**
     * 启动流程
     * 启动流程时会同时增加一条业务数据
     *
     * @param pBusinessKey     业务主键
     * @param pFlowid          流程ID
     * @param pUserID          用户ID
     * @param pSelectVariables 启动变量
     * @return 返回业务主键
     * @throws APPErrorException
     */
    public String[] startFlow(String pBusinessKey, String pFlowid, String pUserID, Map<String, Object> pSelectVariables) throws APPErrorException {
        return startFlow(pBusinessKey, pFlowid, pUserID, pSelectVariables, true);
    }

    /**
     * 启动流程
     * 启动流程时会同时增加一条业务数据
     *
     * @param pBusinessKey     业务主键
     * @param pFlowid          流程ID
     * @param pUserID          用户ID
     * @param pSelectVariables 启动变量
     * @param useClaimTask     是否签收
     * @return 返回业务主键
     * @throws APPErrorException
     */
    public String[] startFlow(String pBusinessKey, String pFlowid, String pUserID, Map<String, Object> pSelectVariables, boolean useClaimTask) throws APPErrorException {
        if (StringUtil.isNullOrEmpty(pBusinessKey)) {
            throw new APPErrorException("无效的业务ID");
        }
        if (StringUtil.isNullOrEmpty(pFlowid)) {
            throw new APPErrorException("无效的processID");
        }


        if (StringUtil.isNullOrEmpty(pFlowid)) {
            throw new APPErrorException("无法获取流程ID");
        }
        String id = pBusinessKey;
        startFlowBykey(pFlowid, id, pSelectVariables);

        Task task = getTasks(id).get(0);
        String taskid = task.getId();
        task.setDescription("流程启动");
        ProcessEngines.getDefaultProcessEngine().getTaskService().saveTask(task);

        if (useClaimTask) {
            claimTask(taskid, pUserID);
        }

        return new String[]{id, taskid};
    }

    /**
     * 删除流程实例
     * 删除一个正在处理的流程
     *
     * @param pBusinessKey 业务ID
     * @param pUserID      业务ID
     * @throws APPErrorException
     */
    public void deleteFlowByKey(String pBusinessKey, String pUserID) throws APPErrorException {
        if (StringUtil.isNullOrEmpty(pBusinessKey)) {
            throw new APPErrorException("无效的业务ID");
        }
        String procssid = getHistoricProcessInstance(pBusinessKey).getId();
        if (StringUtil.isNullOrEmpty(procssid)) {
            throw new APPErrorException("无效的流程实例ID");
        }
        ProcessEngines.getDefaultProcessEngine().getRuntimeService().deleteProcessInstance(procssid, "用户：" + pUserID + "手动删除");
    }

    /**
     * 删除流程实例
     * 删除一个正在处理的流程
     *
     * @param pBusinessKey 业务ID
     * @param pUserID      业务ID
     * @throws APPErrorException
     */
    @Deprecated
    public void deleteFlowBykey(String pBusinessKey, String pUserID) throws APPErrorException {
        deleteFlowBykey(pBusinessKey, pUserID);
    }

    /**
     * 发送流程
     *
     * @param pIsBack          true:回退，false 向下扭转
     * @param pBusinessKey     业务ID
     * @param pTaskId          taskID
     * @param pUserID          用户ID
     * @param pSelectVariables 选中的节点 或 扭转的变量信息
     * @throws APPErrorException
     */
    public void sendFlow(boolean pIsBack, String pBusinessKey, String pTaskId, String pUserID, Map<String, Object> pSelectVariables) throws APPErrorException {
        sendFlow(pIsBack, pBusinessKey, pTaskId, pUserID, pSelectVariables, false);
    }

    /**
     * 发送流程
     *
     * @param pIsBack          true:回退，false 向下扭转
     * @param pBusinessKey     业务ID
     * @param pTaskId          taskID
     * @param pUserID          用户ID
     * @param pSelectVariables 选中的节点 或 扭转的变量信息
     * @param pLocalScope      是否使用局部变量，pSelectVariables 只在本次提交有效
     * @throws APPErrorException
     */
    public void sendFlow(boolean pIsBack, String pBusinessKey, String pTaskId, String pUserID, Map<String, Object> pSelectVariables, boolean pLocalScope) throws APPErrorException {
        sendFlow(pIsBack, pBusinessKey, pTaskId, pUserID, pSelectVariables, pLocalScope, true);
    }

    /**
     * 发送流程
     *
     * @param pIsBack          true:回退，false 向下扭转
     * @param pBusinessKey     业务ID
     * @param pTaskid          taskID
     * @param pUserID          用户ID
     * @param pSelectVariables 选中的节点 或 扭转的变量信息
     * @param pLocalScope      是否使用局部变量，pSelectVariables 只在本次提交有效
     * @param useClaimTask     是否签收
     * @throws APPErrorException
     */
    public void sendFlow(boolean pIsBack, String pBusinessKey, String pTaskid, String pUserID, Map<String, Object> pSelectVariables, boolean pLocalScope, boolean useClaimTask) throws APPErrorException {
        HistoricProcessInstance hpi = getHistoricProcessInstance(pBusinessKey);
        String pdi = hpi.getProcessDefinitionId();
        String procid = hpi.getId();


        FlowElementExtCollection list = getNextTaskInfo(pTaskid, pBusinessKey);

        Map<String, Object> vars = list.toNegativeVariables();
        for (Map.Entry<String, Object> item : pSelectVariables.entrySet()) {
            vars.put(item.getKey(), item.getValue());
        }

        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        Task task = taskService.createTaskQuery().taskId(pTaskid).singleResult();
        task.setDescription(pIsBack ? "用户回退" : "用户发送");
        taskService.saveTask(task);
        if (!pIsBack) {
            nextFlow(pTaskid, pUserID, vars, pLocalScope, useClaimTask);
        } else {
            backFlow(pBusinessKey, pTaskid, pUserID, vars, pLocalScope, useClaimTask);
        }
    }

    /**
     * 作废流程
     *
     * @param pTaskID      流程ID
     * @param pBusinessKey 业务ID
     * @param pUserID      用户ID
     * @throws APPErrorException
     */
    public void abortFlow(String pTaskID, String pBusinessKey, String pUserID) throws APPErrorException {
        abortFlow(pTaskID, pBusinessKey, pUserID, true);
    }

    /**
     * 作废流程
     *
     * @param pTaskID      流程ID
     * @param pBusinessKey 业务ID
     * @param pUserID      用户ID
     * @param useClaimTask 是否签收
     * @throws APPErrorException
     */
    public void abortFlow(String pTaskID, String pBusinessKey, String pUserID, boolean useClaimTask) throws APPErrorException {
        validStringParam("pTaskID", pTaskID);
        validStringParam("pBusinessKey", pBusinessKey);
        validStringParam("pUserID", pUserID);

        Task task = ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().taskId(pTaskID).singleResult();
        if (null == task) {
            throw new APPErrorException("找不到任务信息");
        }
        task.setDescription("用户作废");
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        taskService.saveTask(task);

        if (useClaimTask) {
            claimTask(pTaskID, pUserID);
        }

        ProcessEngines.getDefaultProcessEngine().getRuntimeService().deleteProcessInstance(task.getProcessInstanceId(), "用户作废");
        taskService.deleteTask(pTaskID, "用户作废");
        // ActivityImpl endActivity = findActivitiImpl(pTaskID, "end");
        //turnTransition(pTaskID, pBusinessKey, pUserID, endActivity.getId(), null);
    }

    /**
     * 删除流程
     *
     * @param p_BusinessKey
     * @param p_UserID
     * @throws APPErrorException
     */
    public void abortFlow(String p_BusinessKey, String p_UserID) throws APPErrorException {
        //validStringParam("p_TaskID", p_TaskID);
        validStringParam("p_BusinessKey", p_BusinessKey);
        validStringParam("p_UserID", p_UserID);
        //通过业务ID 获取 当前所有任务
        List<Task> list = ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().processInstanceBusinessKey(p_BusinessKey).list();
        String processInstanceId = null;
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            task.setAssignee(p_UserID);
            task.setDescription("用户作废");
            //保存任务信息
            taskService.saveTask(task);
        }
        //删除流程实例
        ProcessEngines.getDefaultProcessEngine().getRuntimeService().deleteProcessInstance(processInstanceId, "用户作废");
        for (int i = 0; i < list.size(); i++) {
            Task task = list.get(i);
            //删除任务
            taskService.deleteTask(task.getId(), "用户作废");
        }
    }

    /**
     * 流程流转到下一节点
     *
     * @param pTaskid
     * @param pUserID
     * @param pVariables
     */
    public void nextFlow(String pTaskid, String pUserID, Map<String, Object> pVariables) throws APPErrorException {
        nextFlow(pTaskid, pUserID, pVariables, false, true);
    }

    /**
     * 流程流转到下一节点
     *
     * @param pTaskid
     * @param pUserID
     * @param pVariables
     * @param pLocalScope  是否使用局部变量，pSelectVariables 只在本次提交有效
     * @param useClaimTask 是否签收
     */
    public void nextFlow(String pTaskid, String pUserID, Map<String, Object> pVariables, boolean pLocalScope, boolean useClaimTask) throws APPErrorException {
        validStringParam("pTaskid", pTaskid);
        validStringParam("pUserID", pUserID);


        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();

        if (useClaimTask) {
            claimTask(pTaskid, pUserID);//签收
        }

        taskService.complete(pTaskid, pVariables, pLocalScope);//流转

    }

    /**
     * 回退流程
     * 如果回退的是第一个usertask 则将自动签收
     *
     * @param pTaskid
     * @param pUserID
     * @param pVariables
     * @throws APPErrorException
     */
    public void backFlow(String pBusinessKey, String pTaskid, String pUserID, Map<String, Object> pVariables) throws APPErrorException {
        backFlow(pBusinessKey, pTaskid, pUserID, pVariables, false, true);
    }

    /**
     * 回退流程
     * 如果回退的是第一个usertask 则将自动签收
     *
     * @param pTaskId
     * @param pUserID
     * @param pVariables
     * @param pLocalScope  是否使用局部变量，pSelectVariables 只在本次提交有效
     * @param useClaimTask 是否签收
     * @throws APPErrorException
     */
    public void backFlow(String pBusinessKey, String pTaskId, String pUserID, Map<String, Object> pVariables, boolean pLocalScope, boolean useClaimTask) throws APPErrorException {
        validStringParam("pBusinessKey", pBusinessKey);
        validStringParam("pTaskid", pTaskId);
        validStringParam("pUserID", pUserID);

        if (pVariables.size() < 1) {
            throw new APPErrorException("无效的回退参数");
        }

        backProcessOtherTask(pTaskId, pBusinessKey);

        nextFlow(pTaskId, pUserID, pVariables, pLocalScope, useClaimTask);
        Task task = getTaskWhenFirstUserTask(pBusinessKey);
        if (null != task) {
            List<HistoricTaskInstance> list = getHistoricTask(pBusinessKey);
            if (list.size() > 0) {
                for (int i = list.size() - 1; i > -1; i--) {
//                    LogHelper.info("", "历史任务定义：" + list.get(i).getTaskDefinitionKey() +
//                                    ";历史任务ID：" + list.get(i).getId() + ";当前任务定义：" + task.getTaskDefinitionKey() +
//                                    ";当前任务ID：" + task.getId()
//                            , "FlowBll.backFlow");

                    if (list.get(i).getTaskDefinitionKey().equals(task.getTaskDefinitionKey()) &&
                            !list.get(i).getId().equals(task.getId())) {
                        String user = list.get(i).getAssignee();
                        task.setAssignee(user);
                        ProcessEngines.getDefaultProcessEngine().getTaskService().saveTask(task);
                        break;
                    }
                }
            }
        }

    }

    /**
     * 回退时处理其他任务，如果含有当前任务是并行或者包含则将其他任务删除。
     *
     * @param pTaskID
     * @param pBusinessKey
     * @throws APPErrorException
     */
    private void backProcessOtherTask(String pTaskID, String pBusinessKey) throws APPErrorException {
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        Task task = taskService.createTaskQuery().taskId(pTaskID).singleResult();
//        LogHelper.info("", "start", "backProcessOtherTask");
        if (null == task) {
            throw new APPErrorException("找不到当前任务");
        }

        //String taskdefid= task.getTaskDefinitionKey();
        BpmnModelParser parse = BpmnModelParser.getInstance(getBpmnByTaskID(pTaskID));
        FlowElement preele = parse.getPreElement(task.getTaskDefinitionKey());
        //LogHelper.info("", "获取上一节点ID：" + preele.getId() + ";节点:" + preele.toString(), "backProcessOtherTask");
        //如果上一个节点是并行网关或者包含网关
        if (!parse.isInclusiveGateway(preele) && !parse.isParallelGateway(preele)) {
            return;
        }
        List<Task> tasks = getTasks(pBusinessKey);
        //LogHelper.info("", "获取当前任务集合：" + tasks.size(), "backProcessOtherTask");
        for (int i = 0; i < tasks.size(); i++) {
            String othertaskid = tasks.get(i).getId();
            //LogHelper.info("", "获取当前任务ID：" + tasks.get(i).getId() + ";", "backProcessOtherTask");
            //跳过当前任务
            if (othertaskid.equals(pTaskID)) {
                continue;
            }
            FlowElement pre = parse.getPreElement(tasks.get(i).getTaskDefinitionKey());
            //LogHelper.info("", "获取当前任务的上一个元素ID：" + pre.getId() + ";string:" + pre.toString(), "backProcessOtherTask");
            //如果上一个节点和当前任务的上一节点一样则删除
            if (!parse.isInclusiveGateway(pre) && !parse.isParallelGateway(pre)) {
                continue;
            }

            TaskEntity othertask = (TaskEntity) tasks.get(i);
            //LogHelper.info("", "判断上一元素和当前任务的上一个元素是否一致preeleID：" + preele.getId() + ";preID:" + pre.getId(), "backProcessOtherTask");
            if (preele.getId().equals(pre.getId())) {
                deleteTask(othertask);
            }

        }
    }

    /**
     * 删除任务 同时删除历史和子任务
     *
     * @param pTaskID
     */
    public void deleteTask(String pTaskID) {
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        TaskEntity task = (TaskEntity) taskService.createTaskQuery().taskId(pTaskID).singleResult();
        deleteTask(task);
    }

    /**
     * 删除任务 同时删除历史和子任务
     *
     * @param pTask
     */
    public void deleteTask(TaskEntity pTask) {
        TaskService taskService = ProcessEngines.getDefaultProcessEngine().getTaskService();
        //LogHelper.info("", "开始删除任务；任务ID：" + pTask.getId(), "deleteTask");
        pTask.setExecutionId(null);
        taskService.saveTask(pTask);
        taskService.deleteTask(pTask.getId(), true);
        // LogHelper.info("", "结束删除任务；任务ID：" + pTask.getId(), "deleteTask");
    }

    /**
     * 根据业务获取所有task
     *
     * @param pBusinessKey
     * @return
     * @throws APPErrorException
     */
    public List<Task> getTasks(String pBusinessKey) throws APPErrorException {
        validStringParam("pBusinessKey", pBusinessKey);
        return ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().processInstanceBusinessKey(pBusinessKey).list();
    }

    /**
     * 根据业务和TaskDefID获取所有task
     *
     * @param pBusinessKey
     * @param pTaskDefID
     * @return
     * @throws APPErrorException
     */
    public Task getTask(String pBusinessKey, String pTaskDefID) throws APPErrorException {
        List<Task> tasks = getTasks(pBusinessKey);
        //LogHelper.info("", "tasks size:" + tasks.size(), "FlowBll.getTask");
        for (int i = 0, count = tasks.size(); i < count; i++) {
            //LogHelper.info("", "task DefinitionKey:" + tasks.get(i).getTaskDefinitionKey(), "FlowBll.getTask");
            if (tasks.get(i).getTaskDefinitionKey().equals(pTaskDefID)) {
                return tasks.get(i);
            }
        }
        return null;
    }

    /**
     * 根据业务ID获取 流程处理器
     *
     * @param pBusinessKey
     * @return
     * @throws APPErrorException
     */
    public HistoricProcessInstance getHistoricProcessInstance(String pBusinessKey) throws APPErrorException {
        return getHistoricProcessInstance(pBusinessKey, false);
    }

    /**
     * 根据业务ID获取历史处理器
     *
     * @param pBusinessKey
     * @return
     */
    public HistoricProcessInstance getHistoricProcessInstance(String pBusinessKey, boolean pIsFinish) throws APPErrorException {
        validStringParam("pBusinessKey", pBusinessKey);
        if (pIsFinish) {
            return ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().processInstanceBusinessKey(pBusinessKey).finished().singleResult();
        } else {
            return ProcessEngines.getDefaultProcessEngine().getHistoryService().createHistoricProcessInstanceQuery().processInstanceBusinessKey(pBusinessKey).unfinished().singleResult();
        }
    }

    /**
     * 根据流程ID获取流程的资源数据 如 xml png
     *
     * @param pDeployid 流程ID
     * @param pFileExt  如 xml png
     * @return
     * @throws APPErrorException
     */
    public byte[] getFlowbpmn(String pDeployid, String pFileExt) throws APPErrorException {
        validStringParam("pDeployid", pDeployid);
        validStringParam("pFileExt", pFileExt);


        if (StringUtil.isNullOrEmpty(pDeployid)) {
            throw new APPErrorException("无效的流程ID");
        }
        if (StringUtil.isNullOrEmpty(pFileExt)) {
            throw new APPErrorException("无效的文件扩展名");
        }
        RepositoryService repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();
        List<String> names = repositoryService.getDeploymentResourceNames(pDeployid);
        if (null == names || names.size() < 1) {
            throw new APPErrorException("该流程没有附件资源");
        }
        String imageName = null;
        for (String name : names) {
            if (name.indexOf("." + pFileExt) >= 0) {
                imageName = name;
            }
        }
        if (StringUtil.isNullOrEmpty(imageName)) {
            throw new APPErrorException("找不到资源文件");
        }
        if (imageName != null) {

            // 通过部署ID和文件名称得到文件的输入流
            InputStream in = repositoryService.getResourceAsStream(pDeployid, imageName);
            try {
                byte[] data = readBytes(in);
                return data;
            } catch (Exception e) {
                throw new APPErrorException("流读取错误", e);
            }
        }
        return null;
    }

    /**
     * 获取历史流程高亮图片 高亮所有已处理节点
     *
     * @param pBusinessKey 业务主键
     * @return 返回png图片字节流
     * @throws APPErrorException
     */
    public byte[] getHistoryHighlightImg(String pBusinessKey) throws APPErrorException {
        List<HistoricTaskInstance> list = getHistoricTask(pBusinessKey);
        if (list.size() < 1) {
            throw new APPErrorException("获取高亮图片错误:找不到历史任务信息");
        }
        HistoricTaskInstance task = list.get(0);
        ProcessEngineConfiguration processEngineConfiguration = ProcessEngines.getDefaultProcessEngine().getProcessEngineConfiguration();
        String flowid = task.getProcessDefinitionId();
        try {
            List<String> li = new ArrayList<String>();
            for (int i = 0; i < list.size(); i++) {
                li.add(list.get(i).getTaskDefinitionKey());
            }

            ProcessDiagramGenerator processDiagramGenerator = ProcessEngines.getDefaultProcessEngine().getProcessEngineConfiguration().getProcessDiagramGenerator();
            InputStream imageStream =
                    processDiagramGenerator.generateDiagram(
                            ProcessEngines.getDefaultProcessEngine().getRepositoryService().getBpmnModel(flowid),
                            "png",
                            li,
                            Collections.<String>emptyList(),
                            processEngineConfiguration.getActivityFontName(),
                            processEngineConfiguration.getLabelFontName(),
                            processEngineConfiguration.getAnnotationFontName(),
                            null, 1.0, false
                    );
            byte[] data = readBytes(imageStream);
            return data;
        } catch (Exception e) {
            throw new APPErrorException("获取高亮图片错误", e);
        }

    }

    /**
     * 获取高亮图片 指定节点
     *
     * @param pBusinessKey 业务主键
     * @param pTaskDefID   指定任务节点
     * @return 返回png图片字节流
     * @throws APPErrorException
     */
    public byte[] getHighlightImg(String pBusinessKey, String pTaskDefID) throws APPErrorException {
        validStringParam("pBusinessKey", pBusinessKey);
        validStringParam("pTaskDefID", pTaskDefID);

        HistoricTaskInstance task = getHistoricTask(pBusinessKey, pTaskDefID);
        //Task task = getTask(pBusinessKey,pTaskDefID);
        if (null == task) {
            throw new APPErrorException("无效法获取任务信息");
        }

        ProcessEngineConfiguration processEngineConfiguration = ProcessEngines.getDefaultProcessEngine().getProcessEngineConfiguration();
        String executionId = task.getExecutionId();
        String flowid = task.getProcessDefinitionId();


        try {
            RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();

            ProcessDiagramGenerator processDiagramGenerator = ProcessEngines.getDefaultProcessEngine().getProcessEngineConfiguration().getProcessDiagramGenerator();
            InputStream imageStream =
                    processDiagramGenerator.generateDiagram(
                            ProcessEngines.getDefaultProcessEngine().getRepositoryService().getBpmnModel(flowid),
                            "png",
                            runtimeService.getActiveActivityIds(executionId),
                            Collections.<String>emptyList(),
                            processEngineConfiguration.getActivityFontName(),
                            processEngineConfiguration.getLabelFontName(),
                            processEngineConfiguration.getAnnotationFontName(),
                            null, 1.0, false
                    );
            byte[] data = readBytes(imageStream);
            return data;
        } catch (Exception e) {
            throw new APPErrorException("获取高亮图片错误", e);
        }

    }

    /**
     * 获取高亮图片 指定节点
     *
     * @param pBusinessKey 业务主键
     * @param pTaskDefIDs  指定高亮的任务节点
     * @return 返回png图片字节流
     * @throws APPErrorException
     */
    public byte[] getHighlightImg(String pBusinessKey, List<String> pTaskDefIDs) throws APPErrorException {
        return getHighlightImg(pBusinessKey, pTaskDefIDs, Collections.<String>emptyList());
    }

    /**
     * 获取高亮图片 指定节点
     *
     * @param pBusinessKey      业务主键
     * @param pTaskDefIDs       指定高亮的任务节点
     * @param pHighLightedFlows 指定高亮的任务节点
     * @return 返回png图片字节流
     * @throws APPErrorException
     */
    public byte[] getHighlightImg(String pBusinessKey, List<String> pTaskDefIDs, List<String> pHighLightedFlows) throws APPErrorException {
        validStringParam("pBusinessKey", pBusinessKey);
        if (pTaskDefIDs.size() < 1) {
            throw new APPErrorException("无效的 pTaskDefIDs");
        }
        if (StringUtil.isNullOrEmpty(pTaskDefIDs.get(0))) {
            throw new APPErrorException("无效的 pTaskDefIDs[0]");
        }

        HistoricTaskInstance task = getHistoricTask(pBusinessKey, pTaskDefIDs.get(0));
        //Task task = getTask(pBusinessKey,pTaskDefID);
        if (null == task) {
            throw new APPErrorException("无效法获取任务信息");
        }

        ProcessEngineConfiguration processEngineConfiguration = ProcessEngines.getDefaultProcessEngine().getProcessEngineConfiguration();
        String flowid = task.getProcessDefinitionId();


        try {
            RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();

            ProcessDiagramGenerator processDiagramGenerator = ProcessEngines.getDefaultProcessEngine().getProcessEngineConfiguration().getProcessDiagramGenerator();
            InputStream imageStream =
                    processDiagramGenerator.generateDiagram(
                            ProcessEngines.getDefaultProcessEngine().getRepositoryService().getBpmnModel(flowid),
                            "png",
                            pTaskDefIDs,
                            pHighLightedFlows,
                            processEngineConfiguration.getActivityFontName(),
                            processEngineConfiguration.getLabelFontName(),
                            processEngineConfiguration.getAnnotationFontName(),
                            null, 1.0, false
                    );
            byte[] data = readBytes(imageStream);
            return data;
        } catch (Exception e) {
            throw new APPErrorException("获取高亮图片错误", e);
        }

    }

    /**
     * 获取下一节点信息
     *
     * @param pTaskID      任务ID
     * @param pBusinessKey 业务ID
     * @return
     */
    public FlowElementExtCollection getNextTaskInfo(String pTaskID, String pBusinessKey) throws APPErrorException {
        validStringParam("pTaskID", pTaskID);
        validStringParam("pBusinessKey", pBusinessKey);

        FlowElementExtCollection result = new FlowElementExtCollection();
        Task task = ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().taskId(pTaskID).singleResult();

        RepositoryService repositoryService = ProcessEngines.getDefaultProcessEngine().getRepositoryService();

        RuntimeService runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();

        ProcessDefinitionEntity def = (ProcessDefinitionEntity)
                ((RepositoryServiceImpl) repositoryService).getDeployedProcessDefinition(task.getProcessDefinitionId());

        BpmnModelParser vBpmnModelParser = BpmnModelParser.getInstance(repositoryService.getBpmnModel(task.getProcessDefinitionId()));
        FlowElement vCurrentFlowElement = vBpmnModelParser.getTaskElement(task.getTaskDefinitionId());
        List<FlowElement> vNextFlowElements = vBpmnModelParser.getNextElements(vCurrentFlowElement);


        for (FlowElement flowElement : vNextFlowElements) {
            String id = flowElement.getId();
            if (task.getTaskDefinitionKey().equals(id)) {
                nextTaskInfo(vBpmnModelParser, flowElement, result);
            }
        }
        return result;
    }

    /**
     * 递归查找task，跳过gateway
     *
     * @param pFlowElement
     * @param pNextTaskInfo
     */
    private void nextTaskInfo(BpmnModelParser pBpmnModelParser, FlowElement pFlowElement, FlowElementExtCollection pNextTaskInfo) {
        List<FlowElement> list = pBpmnModelParser.getNextElements(pFlowElement);

        for (FlowElement flowElement : list) {
            FlowNode node = (FlowNode) flowElement;


            if (flowElement instanceof ExclusiveGateway) {
                pNextTaskInfo.addAll(node.getOutgoingFlows());
            }
            //如果是下一节点是网管则返回网管下的所有节点
            else if (flowElement instanceof Gateway) {
                nextTaskInfo(pBpmnModelParser, flowElement, pNextTaskInfo);
            }
        }
        if (pNextTaskInfo.size() < 1) {
            pNextTaskInfo.addAll(list);
        }
    }

    /**
     * 根据流程ID 获取所有UserTask
     * tiao
     *
     * @param pFlowID 流程ID
     * @return
     * @throws APPErrorException
     */
    public List<UserTask> getAllUserTask(String pFlowID) throws APPErrorException {
        validStringParam("pFlowID", pFlowID);

        BpmnModel model = getBpmn(pFlowID);
        BpmnModelParser parse = BpmnModelParser.getInstance(model);
        return parse.getUserTasks();
    }

    /**
     * 根据流程ID 和index 获取UserTask
     *
     * @param pFlowID
     * @param pIndex
     * @return
     * @throws APPErrorException
     */
    public UserTask getUserTaskByIndex(String pFlowID, int pIndex) throws APPErrorException {
        validStringParam("pFlowID", pFlowID);


        List<UserTask> list = getAllUserTask(pFlowID);
        if (list.size() > pIndex) {
            return list.get(pIndex);
        } else {
            return null;
        }
    }


    /**
     * InputStream 转换成字节集
     *
     * @param in
     * @return
     * @throws IOException
     */
    private byte[] readBytes(InputStream in) throws IOException {
        BufferedInputStream bufin = new BufferedInputStream(in);
        int buffSize = 1024;
        ByteArrayOutputStream out = new ByteArrayOutputStream(buffSize);

        byte[] temp = new byte[buffSize];
        int size = 0;
        while ((size = bufin.read(temp)) != -1) {
            out.write(temp, 0, size);
        }
        bufin.close();

        byte[] content = out.toByteArray();
        return content;
    }


    private void validStringParam(String pParamName, String pParamValue) throws APPErrorException {
        if (StringUtil.isNullOrEmpty(pParamValue)) {
            throw new APPErrorException("参数：" + pParamName + "；不能为空");
        }
    }
}
