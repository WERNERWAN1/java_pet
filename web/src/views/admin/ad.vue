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
          rowKey="id"
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
          <template v-if="column.key === 'imageUrl'">
            <img v-if="text" :src="text" style="width: 60px; height: 40px; object-fit: cover;" />
            <span v-else style="color: #ccc;">无</span>
          </template>
          <template v-if="column.key === 'operation'">
            <span>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical"/>
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
                <a-form-item label="横幅图片">
                  <a-upload-dragger
                      name="file"
                      accept="image/*"
                      :multiple="false"
                      :before-upload="beforeUpload"
                      :file-list="fileList"
                      @remove="handleRemove"
                  >
                    <p class="ant-upload-drag-icon">
                      <template v-if="modal.form.imageUrl">
                        <img :src="modal.form.imageUrl" style="width: 80px; height: 50px; object-fit: cover;" />
                      </template>
                      <template v-else>
                        <file-image-outlined />
                      </template>
                    </p>
                    <p class="ant-upload-text">请选择要上传的横幅图片</p>
                  </a-upload-dragger>
                </a-form-item>
              </a-col>
              <a-col span="24">
                <a-form-item label="跳转链接" name="link">
                  <a-input placeholder="请输入" v-model:value="modal.form.link"/>
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
import {createApi, listApi, updateApi, deleteApi} from '/@/api/ad';
import {BASE_URL} from "/@/store/constants";
import {FileImageOutlined} from '@ant-design/icons-vue';

const columns = reactive([
  {title: '序号', dataIndex: 'index', key: 'index', align: 'center', width: 60},
  {title: '图片', dataIndex: 'imageUrl', key: 'imageUrl', align: 'center', width: 100},
  {title: '跳转链接', dataIndex: 'link', key: 'link', align: 'center'},
  {title: '操作', dataIndex: 'action', key: 'operation', align: 'center', fixed: 'right', width: 140},
]);

const fileList = ref<any[]>([]);

const beforeUpload = (file: File) => {
  const fileName = new Date().getTime().toString() + '.' + file.type.substring(6);
  const copyFile = new File([file], fileName);
  modal.form.imageFile = copyFile;
  // 本地预览
  const reader = new FileReader();
  reader.onload = (e) => { modal.form.imageUrl = e.target?.result as string; };
  reader.readAsDataURL(file);
  fileList.value = [file];
  return false;
};

const handleRemove = () => {
  modal.form.imageFile = undefined;
  modal.form.imageUrl = undefined;
  fileList.value = [];
};

const data = reactive({
  list: [] as any[],
  loading: false,
  keyword: '',
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
});

const modal = reactive({
  visile: false,
  editFlag: false,
  title: '',
  form: {id: undefined as any, image: undefined as any, imageFile: undefined as any, imageUrl: undefined as any, link: undefined as any},
  rules: {link: [{required: true, message: '请输入', trigger: 'change'}]},
});

const myform = ref<FormInstance>();

onMounted(() => { getList(); });

const getList = () => {
  data.loading = true;
  listApi({keyword: data.keyword}).then((res) => {
    data.loading = false;
    res.data.forEach((item: any, index: number) => {
      item.index = index + 1;
      if (item.image) {
        item.imageUrl = BASE_URL + '/api/staticfiles/image/' + item.image;
      }
    });
    data.list = res.data;
  }).catch(() => { data.loading = false; });
};

const rowSelection = ref({onChange: (selectedRowKeys: (string | number)[]) => { data.selectedRowKeys = selectedRowKeys; }});

const handleAdd = () => {
  resetModal();
  modal.visile = true;
  modal.editFlag = false;
  modal.title = '新增';
  for (const key in modal.form) { modal.form[key] = undefined; }
  fileList.value = [];
};

const handleEdit = (record: any) => {
  resetModal();
  modal.visile = true;
  modal.editFlag = true;
  modal.title = '编辑';
  for (const key in modal.form) { modal.form[key] = undefined; }
  for (const key in record) { modal.form[key] = record[key]; }
  // 编辑时显示已有图片预览
  if (modal.form.image) {
    modal.form.imageUrl = BASE_URL + '/api/staticfiles/image/' + modal.form.image;
  }
  fileList.value = [];
};

const confirmDelete = (record: any) => {
  deleteApi({ids: record.id}).then(() => { getList(); }).catch((err) => { message.error(err.msg || '操作失败'); });
};

const handleBatchDelete = () => {
  if (data.selectedRowKeys.length <= 0) { message.warn('请勾选删除项'); return; }
  deleteApi({ids: data.selectedRowKeys.join(',')}).then(() => {
    message.success('删除成功');
    data.selectedRowKeys = [];
    getList();
  }).catch((err) => { message.error(err.msg || '操作失败'); });
};

const handleOk = () => {
  myform.value?.validate().then(() => {
    const formData = new FormData();
    if (modal.form.id) {
      formData.append('id', modal.form.id);
    }
    if (modal.form.link) {
      formData.append('link', modal.form.link);
    }
    if (modal.form.imageFile) {
      formData.append('imageFile', modal.form.imageFile);
    }
    const api = modal.editFlag ? updateApi : createApi;
    api(formData).then(() => { hideModal(); getList(); }).catch((err) => { message.error(err.msg || '操作失败'); });
  }).catch(() => { console.log('不能为空'); });
};

const handleCancel = () => { hideModal(); };
const resetModal = () => { myform.value?.resetFields(); fileList.value = []; };
const hideModal = () => { modal.visile = false; };
</script>

<style scoped lang="less">
.page-view { min-height: 100%; background: #fff; padding: 24px; display: flex; flex-direction: column; }
.table-operations { margin-bottom: 16px; text-align: right; }
.table-operations > button { margin-right: 8px; }
</style>