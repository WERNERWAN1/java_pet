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
          rowKey="pid"
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
          <template v-if="column.key === 'photo'">
            <img v-if="text" :src="getImgUrl(text)" style="width: 60px; height: 60px; object-fit: cover;" />
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
                <a-form-item label="宠物名称" name="nickname">
                  <a-input placeholder="请输入名称" v-model:value="modal.form.nickname"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="性别" name="gender">
                  <a-select placeholder="请选择性别" v-model:value="modal.form.gender">
                    <a-select-option value="公">公</a-select-option>
                    <a-select-option value="母">母</a-select-option>
                  </a-select>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="年龄" name="years">
                  <a-input-number placeholder="请输入年龄" v-model:value="modal.form.years" style="width: 100%;" :min="0"></a-input-number>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="品种" name="breed">
                  <a-input placeholder="请输入品种" v-model:value="modal.form.breed"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="12">
                <a-form-item label="健康状态" name="condition">
                  <a-input placeholder="请输入健康状态" v-model:value="modal.form.condition"></a-input>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="宠物介绍" name="introduction">
                  <a-textarea placeholder="请输入介绍" :rows="3" v-model:value="modal.form.introduction"></a-textarea>
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
import {createApi, listApi, updateApi, deleteApi} from '/@/api/animal';
import {BASE_URL} from "/@/store/constants";

const getImgUrl = (img) => BASE_URL + '/api/staticfiles/image/' + img;

const columns = reactive([
  {title: '序号', dataIndex: 'index', key: 'index', align: 'center'},
  {title: '照片', dataIndex: 'photo', key: 'photo', align: 'center'},
  {title: '名称', dataIndex: 'nickname', key: 'nickname', align: 'center'},
  {title: '性别', dataIndex: 'gender', key: 'gender', align: 'center'},
  {title: '年龄', dataIndex: 'years', key: 'years', align: 'center'},
  {title: '品种', dataIndex: 'breed', key: 'breed', align: 'center'},
  {title: '健康状态', dataIndex: 'condition', key: 'condition', align: 'center'},
  {title: '介绍', dataIndex: 'introduction', key: 'introduction', align: 'center', ellipsis: true},
  {title: '操作', dataIndex: 'action', key: 'operation', align: 'center', fixed: 'right', width: 140},
]);

const data = reactive({
  list: [],
  loading: false,
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
});

const modal = reactive({
  visile: false,
  editFlag: false,
  title: '',
  form: {pid: undefined, nickname: undefined, gender: undefined, years: undefined, breed: undefined, condition: undefined, introduction: undefined, photo: undefined},
  rules: {nickname: [{required: true, message: '请输入', trigger: 'change'}]},
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

const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[]) => { data.selectedRowKeys = selectedRowKeys; },
});

const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增宠物';
  for (const key in modal.form) { modal.form[key] = undefined; }
};

const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑宠物';
  for (const key in modal.form) { modal.form[key] = undefined; }
  for (const key in record) { modal.form[key] = record[key]; }
};

const confirmDelete = (record: any) => {
  deleteApi({ids: record.pid}).then(() => { getDataList(); }).catch((err) => { message.error(err.msg || '操作失败'); });
};

const handleBatchDelete = () => {
  if (data.selectedRowKeys.length <= 0) { message.warn('请勾选删除项'); return; }
  deleteApi({ids: data.selectedRowKeys.join(',')}).then(() => {
    message.success('删除成功');
    data.selectedRowKeys = [];
    getDataList();
  }).catch((err) => { message.error(err.msg || '操作失败'); });
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