<template>
  <div>
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <a-input-search
              v-model:value="data.keyword"
              placeholder="搜索宠物名称"
              style="width: 250px;"
              @search="handleSearch"
              allow-clear
          />
          <a-button type="primary" @click="handleAdd">新增</a-button>
          <a-button @click="handleBatchDelete">批量删除</a-button>
        </a-space>
      </div>
      <a-table
          size="middle"
          rowKey="pid"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.pageData"
          :scroll="{ x: 'max-content' }"
          :row-selection="rowSelection"
          :pagination="pagination"
      >
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.key === 'photo'">
            <img v-if="record.photo" :src="getImgUrl(record.photo)" style="width: 60px; height: 60px; object-fit: cover;" />
            <span v-else style="color: #ccc;">暂无图片</span>
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
              <a-col span="24">
                <a-form-item label="宠物照片">
                  <a-upload-dragger
                      name="file"
                      accept="image/*"
                      :multiple="false"
                      :before-upload="beforeUpload"
                      :file-list="fileList"
                      @remove="handleRemove"
                  >
                    <p class="ant-upload-drag-icon">
                      <template v-if="modal.form.photoUrl">
                        <img :src="modal.form.photoUrl" style="width: 60px; height: 60px; object-fit: cover;" />
                      </template>
                      <template v-else>
                        <file-image-outlined />
                      </template>
                    </p>
                    <p class="ant-upload-text">请选择要上传的宠物照片</p>
                  </a-upload-dragger>
                </a-form-item>
              </a-col>
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
                <a-form-item label="健康状态" name="healthCondition">
                  <a-input placeholder="请输入健康状态" v-model:value="modal.form.healthCondition"></a-input>
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
import {getFormatTime} from "/@/utils";
import {FileImageOutlined} from '@ant-design/icons-vue';

const getImgUrl = (img) => BASE_URL + '/api/staticfiles/image/' + img;

const fileList = ref<any[]>([]);

const beforeUpload = (file: File) => {
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  modal.form.imageFile = copyFile;
  const reader = new FileReader();
  reader.onload = (e) => { modal.form.photoUrl = e.target?.result as string; };
  reader.readAsDataURL(file);
  fileList.value = [file];
  return false;
};

const handleRemove = () => {
  modal.form.imageFile = undefined;
  modal.form.photoUrl = undefined;
  fileList.value = [];
};

const columns = [
  {title: '序号', dataIndex: 'index', key: 'index', align: 'center', width: 60},
  {title: '照片', dataIndex: 'photo', key: 'photo', align: 'center', width: 80},
  {title: '名称', dataIndex: 'nickname', key: 'nickname', align: 'center'},
  {title: '性别', dataIndex: 'gender', key: 'gender', align: 'center'},
  {title: '年龄', dataIndex: 'years', key: 'years', align: 'center'},
  {title: '品种', dataIndex: 'breed', key: 'breed', align: 'center'},
  {title: '健康状态', dataIndex: 'healthCondition', key: 'healthCondition', align: 'center'},
  {title: '介绍', dataIndex: 'introduction', key: 'introduction', align: 'center', ellipsis: true},
  {title: '创建人', dataIndex: 'createByName', key: 'createByName', align: 'center', width: 100, customRender: ({text}) => text || '--'},
  {title: '创建时间', dataIndex: 'createTime', key: 'createTime', align: 'center', width: 170, customRender: ({text}) => getFormatTime(text, true) || '--'},
  {title: '修改人', dataIndex: 'updateByName', key: 'updateByName', align: 'center', width: 100, customRender: ({text}) => text || '--'},
  {title: '修改时间', dataIndex: 'updateTime', key: 'updateTime', align: 'center', width: 170, customRender: ({text}) => getFormatTime(text, true) || '--'},
  {title: '操作', dataIndex: 'action', key: 'operation', align: 'center', fixed: 'right', width: 140},
];

const data = reactive({
  allList: [] as any[],
  pageData: [] as any[],
  loading: false,
  keyword: '',
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
  total: 0,
});

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共${total}条数据`,
  onChange: (page: number, pageSize: number) => {
    data.page = page;
    data.pageSize = pageSize;
    pagination.current = page;
    updatePageData();
  },
});

const updatePageData = () => {
  const start = (data.page - 1) * data.pageSize;
  data.pageData = data.allList.slice(start, start + data.pageSize);
};

const modal = reactive({
  visile: false,
  editFlag: false,
  title: '',
  form: {pid: undefined as any, nickname: undefined as any, gender: undefined as any, years: undefined as any, breed: undefined as any, healthCondition: undefined as any, introduction: undefined as any, photo: undefined as any, photoUrl: undefined as any, imageFile: undefined as any},
  rules: {nickname: [{required: true, message: '请输入', trigger: 'change'}]},
});

const myform = ref<FormInstance>();

onMounted(() => { getDataList(); });

const getDataList = () => {
  data.loading = true;
  listApi({keyword: data.keyword}).then((res) => {
    data.loading = false;
    const list = res.data || [];
    list.forEach((item: any, index: number) => { item.index = index + 1; });
    data.allList = list;
    data.total = list.length;
    pagination.total = list.length;
    data.page = 1;
    pagination.current = 1;
    updatePageData();
  }).catch(() => { data.loading = false; });
};

const handleSearch = () => {
  data.page = 1;
  getDataList();
};

const rowSelection = ref({
  onChange: (selectedRowKeys: (string | number)[]) => { data.selectedRowKeys = selectedRowKeys; },
});

const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增宠物';
  for (const key in modal.form) { (modal.form as any)[key] = undefined; }
  fileList.value = [];
};

const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑宠物';
  for (const key in modal.form) { (modal.form as any)[key] = undefined; }
  for (const key in record) { (modal.form as any)[key] = record[key]; }
  // 先生成预览URL，再清空photo字段（保留photoUrl用于显示）
  if (modal.form.photo) {
    modal.form.photoUrl = getImgUrl(modal.form.photo);
  }
  fileList.value = [];
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
    const formData = new FormData();
    if (modal.editFlag && modal.form.pid) {
      formData.append('pid', modal.form.pid);
    }
    formData.append('nickname', modal.form.nickname || '');
    formData.append('gender', modal.form.gender || '');
    formData.append('years', modal.form.years || '');
    formData.append('breed', modal.form.breed || '');
    formData.append('healthCondition', modal.form.healthCondition || '');
    formData.append('introduction', modal.form.introduction || '');
    if (modal.form.imageFile) {
      formData.append('imageFile', modal.form.imageFile);
    }
    const api = modal.editFlag ? updateApi : createApi;
    api(formData).then(() => { hideModal(); getDataList(); }).catch((err) => { message.error(err.msg || '操作失败'); });
  }).catch(() => { message.error('请填写必填项'); });
};

const handleCancel = () => { hideModal(); };
const resetModal = () => { myform.value?.resetFields(); fileList.value = []; };
const hideModal = () => { modal.visile = false; };
</script>

<style scoped lang="less">
.page-view { min-height: 100%; background: #fff; padding: 24px; display: flex; flex-direction: column; }
.table-operations { margin-bottom: 16px; display: flex; justify-content: flex-start; gap: 8px; }
</style>