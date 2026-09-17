<template>
  <div>
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <a-button type="primary" @click="handleAdd">新增</a-button>
          <a-button @click="handleBatchDelete">批量删除</a-button>
        </a-space>
      </div>
      <a-table
          size="middle"
          rowKey="orderId"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.list"
          :scroll="{ x: 'max-content' }"
          :row-selection="rowSelection"
          :pagination="{
          size: 'default',
          current: data.page,
          pageSize: data.pageSize,
          onChange: (current) => (data.page = current),
          showSizeChanger: false,
          showTotal: (total) => `共${total}条数据`,
        }"
      >
        <template #bodyCell="{ text, record, index, column }">
          <template v-if="column.key === 'progress'">
            <a-tag :color="text === '已完成' ? 'green' : text === '进行中' ? 'blue' : 'orange'">{{ text }}</a-tag>
          </template>
          <template v-if="column.key === 'operation'">
            <span>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
                <a href="#">删除</a>
              </a-popconfirm>
            </span>
          </template>
        </template>
      </a-table>
    </div>

    <div>
      <a-modal
          :visible="modal.visile"
          :forceRender="true"
          :title="modal.title"
          ok-text="确认"
          cancel-text="取消"
          @cancel="handleCancel"
          @ok="handleOk"
          width="600px"
      >
        <div>
          <a-form ref="myform" :label-col="{ style: { width: '80px' } }" :model="modal.form" :rules="modal.rules">
            <a-row :gutter="24">
              <a-col span="12">
                <a-form-item label="委托人ID" name="clientUid">
                  <a-input-number placeholder="请输入用户ID" v-model:value="modal.form.clientUid" style="width: 100%;"></a-input-number>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="宠物名" name="petName">
                  <a-input placeholder="请输入宠物名" v-model:value="modal.form.petName"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="入住日期" name="startDate">
                  <a-input placeholder="请输入日期" v-model:value="modal.form.startDate"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="托管天数" name="duration">
                  <a-input-number placeholder="请输入天数" v-model:value="modal.form.duration" style="width: 100%;" :min="1"></a-input-number>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="服务状态" name="progress">
                  <a-select placeholder="请选择状态" v-model:value="modal.form.progress">
                    <a-select-option value="待处理">待处理</a-select-option>
                    <a-select-option value="进行中">进行中</a-select-option>
                    <a-select-option value="已完成">已完成</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="笼位编号" name="cageNo">
                  <a-input-number placeholder="请输入笼位号" v-model:value="modal.form.cageNo" style="width: 100%;" :min="1"></a-input-number>
                </a-form-item>
              </a-col>
            </a-row>
          </a-form>
        </div>
      </a-modal>
    </div>
  </div>
</template>

<script setup lang="ts">
import {FormInstance, message} from 'ant-design-vue';
import {createApi, listApi, updateApi, deleteApi} from '/@/api/foster';

const columns = reactive([
  {title: '序号', dataIndex: 'index', key: 'index', align: 'center'},
  {title: '委托人', dataIndex: 'client_name', key: 'client_name', align: 'center'},
  {title: '宠物名', dataIndex: 'pet_name', key: 'pet_name', align: 'center'},
  {title: '入住日期', dataIndex: 'start_date', key: 'start_date', align: 'center'},
  {title: '托管天数', dataIndex: 'duration', key: 'duration', align: 'center'},
  {title: '服务状态', dataIndex: 'progress', key: 'progress', align: 'center'},
  {title: '笼位编号', dataIndex: 'cage_no', key: 'cage_no', align: 'center'},
  {title: '操作', dataIndex: 'action', key: 'operation', align: 'center', fixed: 'right', width: 140},
]);

const data = reactive({list: [], loading: false, selectedRowKeys: [] as any[], pageSize: 10, page: 1});

const modal = reactive({
  visile: false, editFlag: false, title: '',
  form: {orderId: undefined, clientUid: undefined, petName: undefined, startDate: undefined, duration: undefined, progress: undefined, cageNo: undefined},
  rules: {clientUid: [{required: true, message: '请输入', trigger: 'change'}], petName: [{required: true, message: '请输入', trigger: 'change'}]},
});

const myform = ref<FormInstance>();
onMounted(() => { getDataList(); });

const getDataList = () => {
  data.loading = true;
  listApi({}).then((res) => {
    data.loading = false;
    res.data.forEach((item: any, index: any) => { item.index = index + 1; });
    data.list = res.data;
  }).catch(() => { data.loading = false; });
};

const rowSelection = ref({onChange: (selectedRowKeys: (string | number)[]) => { data.selectedRowKeys = selectedRowKeys; }});

const handleAdd = () => {
  resetModal(); modal.visile = true; modal.editFlag = false; modal.title = '新增寄养记录';
  for (const key in modal.form) { modal.form[key] = undefined; }
};

const handleEdit = (record: any) => {
  resetModal(); modal.visile = true; modal.editFlag = true; modal.title = '编辑寄养记录';
  for (const key in modal.form) { modal.form[key] = undefined; }
  for (const key in record) { modal.form[key] = record[key]; }
};

const confirmDelete = (record: any) => {
  deleteApi({ids: record.orderId}).then(() => { getDataList(); }).catch((err) => { message.error(err.msg || '操作失败'); });
};

const handleBatchDelete = () => {
  if (data.selectedRowKeys.length <= 0) { message.warn('请勾选删除项'); return; }
  deleteApi({ids: data.selectedRowKeys.join(',')}).then(() => { message.success('删除成功'); data.selectedRowKeys = []; getDataList(); }).catch((err) => { message.error(err.msg || '操作失败'); });
};

const handleOk = () => {
  myform.value?.validate().then(() => {
    const api = modal.editFlag ? updateApi : createApi;
    api(modal.form).then(() => { hideModal(); getDataList(); }).catch((err) => { message.error(err.msg || '操作失败'); });
  }).catch(() => { message.error('请填写必填项'); });
};

const handleCancel = () => { hideModal(); };
const resetModal = () => { myform.value?.resetFields(); };
const hideModal = () => { modal.visile = false; };
</script>

<style scoped lang="less">
.page-view { min-height: 100%; background: #fff; padding: 24px; display: flex; flex-direction: column; }
.table-operations { margin-bottom: 16px; text-align: right; }
.table-operations > button { margin-right: 8px; }
</style>