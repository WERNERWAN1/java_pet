<template>
  <div>
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <a-input-search
              v-model:value="data.keyword"
              placeholder="搜索上报说明"
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
          rowKey="id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.pageData"
          :scroll="{ x: 'max-content' }"
          :row-selection="rowSelection"
          :pagination="pagination"
      >
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.key === 'img'">
            <img v-if="record.img" :src="getImgUrl(record.img)" style="width: 60px; height: 60px; object-fit: cover;" />
            <span v-else style="color: #ccc;">暂无图片</span>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="text === '已处理' ? 'green' : 'orange'">{{ text }}</a-tag>
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
                <a-form-item label="上报图片">
                  <a-upload-dragger
                      name="file"
                      accept="image/*"
                      :multiple="false"
                      :before-upload="beforeUpload"
                      :file-list="fileList"
                      @remove="handleRemove"
                  >
                    <p class="ant-upload-drag-icon">
                      <template v-if="modal.form.imgUrl">
                        <img :src="modal.form.imgUrl" style="width: 60px; height: 60px; object-fit: cover;" />
                      </template>
                      <template v-else>
                        <file-image-outlined />
                      </template>
                    </p>
                    <p class="ant-upload-text">请选择要上传的图片</p>
                  </a-upload-dragger>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="上报说明" name="name">
                  <a-textarea placeholder="请输入说明" :rows="3" v-model:value="modal.form.name"></a-textarea>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="上报时间" name="time">
                  <a-date-picker placeholder="请选择上报时间" v-model:value="modal.form.time" value-format="YYYY-MM-DD HH:mm:ss" show-time style="width: 100%;" />
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="处理状态" name="status">
                  <a-select placeholder="请选择状态" v-model:value="modal.form.status">
                    <a-select-option value="待处理">待处理</a-select-option>
                    <a-select-option value="已处理">已处理</a-select-option>
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
import {createApi, listApi, updateApi, deleteApi} from '/@/api/submit';
import {BASE_URL} from "/@/store/constants";
import {FileImageOutlined} from '@ant-design/icons-vue';

const getImgUrl = (img) => BASE_URL + '/api/staticfiles/image/' + img;

const fileList = ref<any[]>([]);

const beforeUpload = (file: File) => {
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  modal.form.imageFile = copyFile;
  const reader = new FileReader();
  reader.onload = (e) => { modal.form.imgUrl = e.target?.result as string; };
  reader.readAsDataURL(file);
  fileList.value = [file];
  return false;
};

const handleRemove = () => {
  modal.form.imageFile = undefined;
  modal.form.imgUrl = undefined;
  fileList.value = [];
};

const columns = [
  {title: '序号', dataIndex: 'index', key: 'index', align: 'center', width: 60},
  {title: '上报说明', dataIndex: 'name', key: 'name', align: 'center', ellipsis: true},
  {title: '上报图片', dataIndex: 'img', key: 'img', align: 'center', width: 80},
  {title: '上报时间', dataIndex: 'time', key: 'time', align: 'center'},
  {title: '处理状态', dataIndex: 'status', key: 'status', align: 'center'},
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
  visile: false, editFlag: false, title: '',
  form: {id: undefined as any, name: undefined as any, time: undefined as any, status: undefined as any, img: undefined as any, imgUrl: undefined as any, imageFile: undefined as any},
  rules: {name: [{required: true, message: '请输入', trigger: 'change'}]},
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

const rowSelection = ref({onChange: (selectedRowKeys: (string | number)[]) => { data.selectedRowKeys = selectedRowKeys; }});

const handleAdd = () => {
  resetModal(); modal.visile = true; modal.editFlag = false; modal.title = '新增上报';
  for (const key in modal.form) { (modal.form as any)[key] = undefined; }
  fileList.value = [];
};

const handleEdit = (record: any) => {
  resetModal(); modal.visile = true; modal.editFlag = true; modal.title = '编辑上报';
  for (const key in modal.form) { (modal.form as any)[key] = undefined; }
  for (const key in record) { (modal.form as any)[key] = record[key]; }
  if (modal.form.img) {
    modal.form.imgUrl = getImgUrl(modal.form.img);
  }
  fileList.value = [];
};

const confirmDelete = (record: any) => {
  deleteApi({ids: record.id}).then(() => { getDataList(); }).catch((err) => { message.error(err.msg || '操作失败'); });
};

const handleBatchDelete = () => {
  if (data.selectedRowKeys.length <= 0) { message.warn('请勾选删除项'); return; }
  deleteApi({ids: data.selectedRowKeys.join(',')}).then(() => { message.success('删除成功'); data.selectedRowKeys = []; getDataList(); }).catch((err) => { message.error(err.msg || '操作失败'); });
};

const handleOk = () => {
  myform.value?.validate().then(() => {
    const formData = new FormData();
    if (modal.editFlag && modal.form.id) {
      formData.append('id', modal.form.id);
    }
    formData.append('name', modal.form.name || '');
    formData.append('time', modal.form.time || '');
    formData.append('status', modal.form.status || '');
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