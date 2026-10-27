<template>
  <div>
    <div class="page-view">
      <div class="table-operations">
        <a-space>
          <a-input-search v-model:value="data.keyword" placeholder="搜索房间编号" style="width: 250px;" @search="handleSearch" allow-clear />
          <a-button type="primary" @click="handleAdd">新增</a-button>
          <a-button @click="handleBatchDelete">批量删除</a-button>
        </a-space>
      </div>
      <a-table size="middle" rowKey="id" :loading="data.loading" :columns="columns" :data-source="data.pageData"
          :scroll="{ x: 'max-content' }" :row-selection="rowSelection" :pagination="pagination">
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.key === 'animalPhoto'">
            <img v-if="text" :src="getImgUrl(text)" style="width: 48px; height: 48px; object-fit: cover; border-radius: 4px;" />
            <span v-else style="color: #ccc;">--</span>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="text === '空闲' ? 'green' : text === '已占用' ? 'red' : 'orange'">{{ text }}</a-tag>
          </template>
          <template v-if="column.key === 'operation'">
            <span>
              <a @click="handleEdit(record)">编辑</a>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)"><a href="#">删除</a></a-popconfirm>
            </span>
          </template>
        </template>
      </a-table>
    </div>
    <div>
      <a-modal :visible="modal.visile" :forceRender="true" :title="modal.title" ok-text="确认" cancel-text="取消" @cancel="handleCancel" @ok="handleOk">
        <a-form ref="myform" :label-col="{ style: { width: '80px' } }" :model="modal.form" :rules="modal.rules">
          <a-row :gutter="24">
            <a-col span="24"><a-form-item label="房间编号" name="name"><a-input placeholder="请输入房间编号" v-model:value="modal.form.name"></a-input></a-form-item></a-col>
            <a-col span="24"><a-form-item label="房间状态" name="status"><a-select placeholder="请选择状态" v-model:value="modal.form.status"><a-select-option value="空闲">空闲</a-select-option><a-select-option value="已占用">已占用</a-select-option><a-select-option value="维护中">维护中</a-select-option></a-select></a-form-item></a-col>
            <a-col span="24"><a-form-item label="寄养宠物" name="animal"><a-input placeholder="请输入寄养宠物名" v-model:value="modal.form.animal"></a-input></a-form-item></a-col>
            <a-col span="24">
              <a-form-item label="宠物照片">
                <a-upload-dragger name="file" accept="image/*" :multiple="false" :before-upload="beforeUpload" :file-list="fileList" @remove="handleRemove">
                  <p class="ant-upload-drag-icon">
                    <template v-if="modal.form.photoUrl">
                      <img :src="modal.form.photoUrl" style="width: 60px; height: 60px; object-fit: cover;" />
                    </template>
                    <template v-else-if="modal.form.animalPhoto">
                      <img :src="getImgUrl(modal.form.animalPhoto)" style="width: 60px; height: 60px; object-fit: cover;" />
                    </template>
                    <template v-else>
                      <file-image-outlined />
                    </template>
                  </p>
                  <p class="ant-upload-text">请选择或拖拽宠物照片</p>
                </a-upload-dragger>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </a-modal>
    </div>
  </div>
</template>

<script setup lang="ts">
import {FormInstance, message} from 'ant-design-vue';
import {createApi, listApi, updateApi, deleteApi} from '/@/api/room';
import {BASE_URL} from "/@/store/constants";
import {getFormatTime} from "/@/utils";
import {FileImageOutlined} from '@ant-design/icons-vue';

const getImgUrl = (img: string) => BASE_URL + '/api/staticfiles/image/' + img;

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
  {title: '房间编号', dataIndex: 'name', key: 'name', align: 'center'},
  {title: '房间状态', dataIndex: 'status', key: 'status', align: 'center'},
  {title: '寄养宠物', dataIndex: 'animal', key: 'animal', align: 'center'},
  {title: '宠物照片', dataIndex: 'animalPhoto', key: 'animalPhoto', align: 'center', width: 80},
  {title: '创建人', dataIndex: 'createByName', key: 'createByName', align: 'center', width: 100, customRender: ({text}) => text || '--'},
  {title: '创建时间', dataIndex: 'createTime', key: 'createTime', align: 'center', width: 170, customRender: ({text}) => getFormatTime(text, true) || '--'},
  {title: '修改人', dataIndex: 'updateByName', key: 'updateByName', align: 'center', width: 100, customRender: ({text}) => text || '--'},
  {title: '修改时间', dataIndex: 'updateTime', key: 'updateTime', align: 'center', width: 170, customRender: ({text}) => getFormatTime(text, true) || '--'},
  {title: '操作', dataIndex: 'action', key: 'operation', align: 'center', fixed: 'right', width: 140},
];

const data = reactive({allList: [] as any[], pageData: [] as any[], loading: false, keyword: '', selectedRowKeys: [] as any[], pageSize: 10, page: 1});
const pagination = reactive({current: 1, pageSize: 10, total: 0, showTotal: (total: number) => `共${total}条数据`,
  onChange: (page: number, pageSize: number) => { data.page = page; data.pageSize = pageSize; pagination.current = page; updatePageData(); },
});
const updatePageData = () => { const start = (data.page - 1) * data.pageSize; data.pageData = data.allList.slice(start, start + data.pageSize); };

const modal = reactive({
  visile: false, editFlag: false, title: '',
  form: {id: undefined as any, name: undefined as any, status: undefined as any, animal: undefined as any, animalPhoto: undefined as any, imageFile: undefined as any, photoUrl: undefined as any},
  rules: {
    name: [
      {required: true, message: '请输入', trigger: 'change'},
      {validator: (_rule: any, value: string) => {
        if (!value) return Promise.resolve();
        const exists = data.allList.some(item => item.name === value && item.id !== modal.form.id);
        return exists ? Promise.reject('房间编号已存在') : Promise.resolve();
      }, trigger: 'blur'},
    ],
    status: [{required: true, message: '请选择', trigger: 'change'}],
  },
});
const myform = ref<FormInstance>();
onMounted(() => { getDataList(); });

const getDataList = () => {
  data.loading = true;
  listApi({keyword: data.keyword}).then((res) => {
    data.loading = false;
    const list = res.data || [];
    list.forEach((item: any, index: number) => { item.index = index + 1; });
    data.allList = list; pagination.total = list.length; data.page = 1; pagination.current = 1; updatePageData();
  }).catch(() => { data.loading = false; });
};
const handleSearch = () => { data.page = 1; getDataList(); };
const rowSelection = ref({onChange: (keys: (string | number)[]) => { data.selectedRowKeys = keys; }});
const handleAdd = () => { resetModal(); modal.visile = true; modal.editFlag = false; modal.title = '新增房间'; for (const key in modal.form) { (modal.form as any)[key] = undefined; } fileList.value = []; };
const handleEdit = (record: any) => { resetModal(); modal.visile = true; modal.editFlag = true; modal.title = '编辑房间'; for (const key in modal.form) { (modal.form as any)[key] = undefined; } for (const key in record) { (modal.form as any)[key] = record[key]; } fileList.value = []; };
const confirmDelete = (record: any) => { deleteApi({ids: record.id}).then(() => { getDataList(); }).catch((err) => { message.error(err.msg || '操作失败'); }); };
const handleBatchDelete = () => { if (data.selectedRowKeys.length <= 0) { message.warn('请勾选删除项'); return; } deleteApi({ids: data.selectedRowKeys.join(',')}).then(() => { message.success('删除成功'); data.selectedRowKeys = []; getDataList(); }).catch((err) => { message.error(err.msg || '操作失败'); }); };
const handleOk = () => {
  myform.value?.validate().then(() => {
    const formData = new FormData();
    if (modal.form.id) formData.append('id', modal.form.id);
    if (modal.form.name) formData.append('name', modal.form.name);
    if (modal.form.status) formData.append('status', modal.form.status);
    if (modal.form.animal) formData.append('animal', modal.form.animal);
    if (modal.form.imageFile) formData.append('imageFile', modal.form.imageFile);
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