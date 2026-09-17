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
          rowKey="recordId"
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
          <template v-if="column.key === 'state'">
            <a-tag :color="text === '已领养' ? 'green' : text === '已退养' ? 'red' : 'blue'">{{ text }}</a-tag>
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
      >
        <div>
          <a-form ref="myform" :label-col="{ style: { width: '80px' } }" :model="modal.form" :rules="modal.rules">
            <a-row :gutter="24">
              <a-col span="24">
                <a-form-item label="领养人ID" name="ownerUid">
                  <a-input-number placeholder="请输入用户ID" v-model:value="modal.form.ownerUid" style="width: 100%;"></a-input-number>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="宠物ID" name="petUid">
                  <a-input-number placeholder="请输入宠物ID" v-model:value="modal.form.petUid" style="width: 100%;"></a-input-number>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="领养日期" name="adoptDate">
                  <a-input placeholder="请输入领养日期" v-model:value="modal.form.adoptDate"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="领养状态" name="state">
                  <a-select placeholder="请选择状态" v-model:value="modal.form.state">
                    <a-select-option value="已领养">已领养</a-select-option>
                    <a-select-option value="已退养">已退养</a-select-option>
                    <a-select-option value="待审核">待审核</a-select-option>
                  </a-select>
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
import {createApi, listApi, updateApi, deleteApi} from '/@/api/adopt';

const columns = reactive([
  {title: '序号', dataIndex: 'index', key: 'index', align: 'center'},
  {title: '领养人', dataIndex: 'owner_name', key: 'owner_name', align: 'center'},
  {title: '宠物名', dataIndex: 'pet_name', key: 'pet_name', align: 'center'},
  {title: '领养日期', dataIndex: 'adopt_date', key: 'adopt_date', align: 'center'},
  {title: '状态', dataIndex: 'state', key: 'state', align: 'center'},
  {title: '操作', dataIndex: 'action', key: 'operation', align: 'center', fixed: 'right', width: 140},
]);

const data = reactive({list: [], loading: false, selectedRowKeys: [] as any[], pageSize: 10, page: 1});

const modal = reactive({
  visile: false, editFlag: false, title: '',
  form: {recordId: undefined, ownerUid: undefined, petUid: undefined, adoptDate: undefined, state: undefined},
  rules: {ownerUid: [{required: true, message: '请输入', trigger: 'change'}], petUid: [{required: true, message: '请输入', trigger: 'change'}]},
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
  resetModal(); modal.visile = true; modal.editFlag = false; modal.title = '新增领养记录';
  for (const key in modal.form) { modal.form[key] = undefined; }
};

const handleEdit = (record: any) => {
  resetModal(); modal.visile = true; modal.editFlag = true; modal.title = '编辑领养记录';
  for (const key in modal.form) { modal.form[key] = undefined; }
  for (const key in record) { modal.form[key] = record[key]; }
};

const confirmDelete = (record: any) => {
  deleteApi({ids: record.recordId}).then(() => { getDataList(); }).catch((err) => { message.error(err.msg || '操作失败'); });
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