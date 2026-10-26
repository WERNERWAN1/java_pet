<template>
  <div>
    <div class="page-view">
      <!-- 搜索栏 -->
      <div class="table-operations">
        <a-space>
          <a-select v-model:value="data.breed" placeholder="选择宠物种类" style="width: 180px;" allow-clear>
            <a-select-option v-for="item in data.breedList" :key="item" :value="item">{{ item }}</a-select-option>
          </a-select>
          <a-select v-model:value="data.state" placeholder="请选择状态" style="width: 150px;" allow-clear>
            <a-select-option value="已领养">已领养</a-select-option>
            <a-select-option value="已退养">已退养</a-select-option>
            <a-select-option value="已归还">已归还</a-select-option>
            <a-select-option value="待审核">待审核</a-select-option>
          </a-select>
          <a-button type="primary" @click="handleSearch">搜索</a-button>
          <a-button style="background: #e6a23c; color: #fff; border-color: #e6a23c;" @click="handleReset">重置</a-button>
        </a-space>
      </div>

      <!-- 数据表格 -->
      <a-table
          size="middle"
          rowKey="record_id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.pageData"
          :scroll="{ x: 'max-content' }"
          :row-selection="rowSelection"
          :pagination="pagination"
      >
        <template #bodyCell="{ text, record, column }">
          <!-- 宠物信息列：圆形头像 + 名称 -->
          <template v-if="column.key === 'pet_info'">
            <div style="display: flex; align-items: center; gap: 8px; justify-content: center;">
              <img v-if="record.pet_photo" :src="getImgUrl(record.pet_photo)" class="pet-avatar" />
              <div v-else class="pet-avatar pet-avatar-placeholder">无</div>
              <span>{{ record.pet_name || '-' }}</span>
            </div>
          </template>
          <!-- 领养状态列 -->
          <template v-if="column.key === 'state'">
            <a-tag :color="getStateColor(text)">{{ text }}</a-tag>
          </template>
          <!-- 操作列 -->
          <template v-if="column.key === 'operation'">
            <a-space>
              <!-- 待审核状态显示审核按钮 -->
              <a-button v-if="record.state === '待审核'" type="primary" size="small" @click="handleAudit(record)">
                审核
              </a-button>
              <a-popconfirm title="确定删除该领养记录?" ok-text="是" cancel-text="否" @confirm="handleDelete(record)">
                <a-button type="link" danger style="border: 1px solid #f56c6c; border-radius: 4px; padding: 0 12px; height: 28px; line-height: 26px;">
                  删除领养
                </a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>

    <!-- 审核弹窗 -->
    <a-modal
        :visible="auditModal.visible"
        title="领养审核"
        ok-text="确认"
        cancel-text="取消"
        @cancel="auditModal.visible = false"
        @ok="handleAuditSubmit"
    >
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="宠物名称">{{ auditModal.record?.pet_name || '-' }}</a-descriptions-item>
        <a-descriptions-item label="宠物种类">{{ auditModal.record?.pet_breed || '-' }}</a-descriptions-item>
        <a-descriptions-item label="性别">{{ auditModal.record?.pet_gender || '-' }}</a-descriptions-item>
        <a-descriptions-item label="年龄">{{ auditModal.record?.pet_years ? auditModal.record.pet_years + '岁' : '-' }}</a-descriptions-item>
        <a-descriptions-item label="健康状态">{{ auditModal.record?.pet_health || '-' }}</a-descriptions-item>
        <a-descriptions-item label="介绍">{{ auditModal.record?.pet_intro || '-' }}</a-descriptions-item>
        <a-descriptions-item label="领养人">{{ auditModal.record?.owner_name || '-' }}</a-descriptions-item>
        <a-descriptions-item label="申请时间">{{ auditModal.record?.adopt_date || '-' }}</a-descriptions-item>
      </a-descriptions>
      <div style="margin-top: 16px;">
        <span style="margin-right: 12px;">审核结果：</span>
        <a-radio-group v-model:value="auditModal.result">
          <a-radio value="已领养">通过</a-radio>
          <a-radio value="已退养">拒绝</a-radio>
        </a-radio-group>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import {message} from 'ant-design-vue';
import {listApi, deleteApi, updateApi} from '/@/api/adopt';
import {listApi as listAnimalApi} from '/@/api/animal';
import {BASE_URL} from "/@/store/constants";
import {getFormatTime} from "/@/utils";

const getImgUrl = (img) => BASE_URL + '/api/staticfiles/image/' + img;

const getStateColor = (state) => {
  return {'已领养': 'green', '已退养': 'red', '已归还': 'orange', '待审核': 'blue'}[state] || 'default';
};

const columns = [
  {title: '序号', dataIndex: 'index', key: 'index', align: 'center', width: 70, sorter: (a, b) => a.index - b.index},
  {title: '宠物信息', dataIndex: 'pet_info', key: 'pet_info', align: 'center', width: 150},
  {title: '宠物种类', dataIndex: 'pet_breed', key: 'pet_breed', align: 'center'},
  {title: '性别', dataIndex: 'pet_gender', key: 'pet_gender', align: 'center', width: 70},
  {title: '年龄', dataIndex: 'pet_years', key: 'pet_years', align: 'center', width: 70},
  {title: '健康状态', dataIndex: 'pet_health', key: 'pet_health', align: 'center'},
  {title: '领养人', dataIndex: 'owner_name', key: 'owner_name', align: 'center'},
  {title: '领养时间', dataIndex: 'adopt_date', key: 'adopt_date', align: 'center', sorter: (a, b) => (a.adopt_date || '').localeCompare(b.adopt_date || '')},
  {title: '领养状态', dataIndex: 'state', key: 'state', align: 'center'},
  {title: '创建人', dataIndex: 'create_by_name', key: 'create_by_name', align: 'center', width: 100, customRender: ({record}) => record.create_by_name || record.createByName || '--'},
  {title: '创建时间', dataIndex: 'create_time', key: 'create_time', align: 'center', width: 170, customRender: ({record}) => getFormatTime(record.create_time || record.createTime, true) || '--'},
  {title: '修改人', dataIndex: 'update_by_name', key: 'update_by_name', align: 'center', width: 100, customRender: ({record}) => record.update_by_name || record.updateByName || '--'},
  {title: '修改时间', dataIndex: 'update_time', key: 'update_time', align: 'center', width: 170, customRender: ({record}) => getFormatTime(record.update_time || record.updateTime, true) || '--'},
  {title: '操作', dataIndex: 'action', key: 'operation', align: 'center', width: 160, fixed: 'right'},
];

const data = reactive({
  allList: [] as any[],
  pageData: [] as any[],
  loading: false,
  breed: undefined as any,
  state: undefined as any,
  breedList: [] as string[],
  selectedRowKeys: [] as any[],
  pageSize: 10,
  page: 1,
});

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共 ${total} 条`,
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

// 审核弹窗
const auditModal = reactive({
  visible: false,
  record: null as any,
  result: '已领养' as string,
});

onMounted(() => {
  getDataList();
  getBreedList();
});

const getDataList = () => {
  data.loading = true;
  listApi({breed: data.breed, state: data.state}).then((res) => {
    data.loading = false;
    const list = res.data || [];
    list.forEach((item: any, index: number) => { item.index = list.length - index; });
    data.allList = list;
    pagination.total = list.length;
    data.page = 1;
    pagination.current = 1;
    updatePageData();
  }).catch(() => { data.loading = false; });
};

const getBreedList = () => {
  listAnimalApi({}).then((res) => {
    const breeds = new Set<string>();
    (res.data || []).forEach((item: any) => {
      if (item.breed) breeds.add(item.breed);
    });
    data.breedList = Array.from(breeds);
  });
};

const handleSearch = () => { data.page = 1; getDataList(); };
const handleReset = () => { data.breed = undefined; data.state = undefined; data.page = 1; getDataList(); };

// 打开审核弹窗
const handleAudit = (record: any) => {
  auditModal.record = record;
  auditModal.result = '已领养';
  auditModal.visible = true;
};

// 提交审核结果
const handleAuditSubmit = () => {
  if (!auditModal.record) return;
  updateApi({
    recordId: auditModal.record.record_id,
    state: auditModal.result,
  }).then(() => {
    message.success('审核完成');
    auditModal.visible = false;
    getDataList();
  }).catch((err) => {
    message.error(err.msg || '审核失败');
  });
};

const handleDelete = (record: any) => {
  deleteApi({ids: record.record_id}).then(() => {
    message.success('删除成功');
    getDataList();
  }).catch((err) => { message.error(err.msg || '操作失败'); });
};

const rowSelection = ref({onChange: (keys: (string | number)[]) => { data.selectedRowKeys = keys; }});
</script>

<style scoped lang="less">
.page-view {
  min-height: 100%;
  background: #fff;
  padding: 24px;
  display: flex;
  flex-direction: column;
}
.table-operations {
  margin-bottom: 16px;
  display: flex;
  justify-content: flex-start;
}
.pet-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  object-fit: cover;
  border: 2px solid #ebeef5;
  flex-shrink: 0;
}
.pet-avatar-placeholder {
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f5f7fa;
  color: #c0c4cc;
  font-size: 12px;
}
</style>