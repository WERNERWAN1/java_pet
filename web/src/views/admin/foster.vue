<template>
  <div>
    <div class="page-view">
      <!-- 筛选查询区 -->
      <div class="table-operations">
        <a-space>
          <a-select v-model:value="data.progress" placeholder="请选择状态" style="width: 180px;" allow-clear>
            <a-select-option value="已领回">已领回</a-select-option>
            <a-select-option value="寄养中">寄养中</a-select-option>
            <a-select-option value="已预约">已预约</a-select-option>
            <a-select-option value="待处理">待处理</a-select-option>
            <a-select-option value="已完成">已完成</a-select-option>
          </a-select>
          <a-button type="primary" @click="handleSearch">查询</a-button>
          <a-button style="background: #fff; color: #e6a23c; border: 1px solid #e6a23c;" @click="handleReset">重置</a-button>
        </a-space>
      </div>

      <!-- 数据列表区 -->
      <a-table
          size="middle"
          rowKey="order_id"
          :loading="data.loading"
          :columns="columns"
          :data-source="data.pageData"
          :scroll="{ x: 'max-content' }"
          :pagination="pagination"
      >
        <template #bodyCell="{ text, record, column }">
          <template v-if="column.key === 'progress'">
            <a-tag :color="getProgressColor(text)">{{ text }}</a-tag>
          </template>
          <template v-if="column.key === 'operation'">
            <a-space>
              <a-button v-if="record.progress === '待处理'" type="primary" size="small" @click="handleAudit(record)">审核</a-button>
              <a-popconfirm title="确定删除该寄养记录?" ok-text="是" cancel-text="否" @confirm="handleDelete(record)">
                <a-button type="link" danger style="border: 1px solid #f56c6c; border-radius: 4px; padding: 0 12px; height: 28px; line-height: 26px;">删除</a-button>
              </a-popconfirm>
            </a-space>
          </template>
        </template>
      </a-table>
    </div>

    <!-- 审核弹窗 -->
    <a-modal
        :visible="auditModal.visible"
        title="寄养审核"
        ok-text="确认"
        cancel-text="取消"
        @cancel="auditModal.visible = false"
        @ok="handleAuditSubmit"
        width="550px"
    >
      <a-descriptions :column="1" bordered size="small">
        <a-descriptions-item label="用户">{{ auditModal.record?.client_name || '-' }}</a-descriptions-item>
        <a-descriptions-item label="宠物昵称">{{ auditModal.record?.pet_name || '-' }}</a-descriptions-item>
        <a-descriptions-item label="入住日期">{{ auditModal.record?.start_date || '-' }}</a-descriptions-item>
        <a-descriptions-item label="寄养天数">{{ auditModal.record?.duration || '-' }}天</a-descriptions-item>
        <a-descriptions-item label="当前状态">
          <a-tag color="red">{{ auditModal.record?.progress || '-' }}</a-tag>
        </a-descriptions-item>
      </a-descriptions>
      <div style="margin-top: 16px;">
        <div style="margin-bottom: 12px;">
          <span style="margin-right: 12px;">审核结果：</span>
          <a-radio-group v-model:value="auditModal.result">
            <a-radio value="寄养中">通过（开始寄养）</a-radio>
            <a-radio value="已预约">通过（已预约）</a-radio>
            <a-radio value="已领回">拒绝（已领回）</a-radio>
          </a-radio-group>
        </div>
        <div>
          <span style="margin-right: 12px;">分配房间：</span>
          <a-select v-model:value="auditModal.cageNo" placeholder="请选择房间" style="width: 200px;" allow-clear>
            <a-select-option v-for="r in roomList" :key="r.id" :value="r.id" :disabled="r.status === '已占用'">
              {{ r.name }} {{ r.status === '已占用' ? '(已占用)' : '' }}
            </a-select-option>
          </a-select>
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script setup lang="ts">
import {message} from 'ant-design-vue';
import {listApi, updateApi, deleteApi} from '/@/api/foster';
import {listApi as listRoomApi} from '/@/api/room';
import {getFormatTime} from "/@/utils";

const getProgressColor = (progress) => {
  return {'已领回': 'green', '已完成': 'green', '寄养中': 'blue', '已预约': 'orange', '待处理': 'red'}[progress] || 'default';
};

const columns = [
  {title: '序号', dataIndex: 'index', key: 'index', align: 'center', width: 80, sorter: (a, b) => a.index - b.index},
  {title: '用户', dataIndex: 'client_name', key: 'client_name', align: 'center'},
  {title: '宠物昵称', dataIndex: 'pet_name', key: 'pet_name', align: 'center'},
  {title: '寄养时间', dataIndex: 'start_date', key: 'start_date', align: 'center', sorter: (a, b) => (a.start_date || '').localeCompare(b.start_date || '')},
  {title: '寄养天数', dataIndex: 'duration', key: 'duration', align: 'center', sorter: (a, b) => (a.duration || 0) - (b.duration || 0)},
  {title: '寄养状态', dataIndex: 'progress', key: 'progress', align: 'center'},
  {title: '寄养房间', dataIndex: 'cage_no', key: 'cage_no', align: 'center'},
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
  progress: undefined as any,
  pageSize: 10,
  page: 1,
});

const pagination = reactive({
  current: 1,
  pageSize: 10,
  total: 0,
  showTotal: (total: number) => `共 ${total} 条`,
  showSizeChanger: false,
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
  result: '寄养中' as string,
  cageNo: undefined as any,
});

// 房间列表
const roomList = ref<any[]>([]);

const loadRooms = () => {
  listRoomApi({}).then((res) => {
    roomList.value = res.data || [];
  });
};

onMounted(() => {
  getDataList();
  loadRooms();
});

const getDataList = () => {
  data.loading = true;
  listApi({progress: data.progress}).then((res) => {
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

const handleSearch = () => { data.page = 1; getDataList(); };
const handleReset = () => { data.progress = undefined; data.page = 1; getDataList(); };

// 打开审核弹窗
const handleAudit = (record: any) => {
  auditModal.record = record;
  auditModal.result = '寄养中';
  auditModal.cageNo = record.cage_no || undefined;
  auditModal.visible = true;
};

// 提交审核
const handleAuditSubmit = () => {
  if (!auditModal.record) return;
  updateApi({
    orderId: auditModal.record.order_id,
    progress: auditModal.result,
    cageNo: auditModal.cageNo || null,
  }).then(() => {
    message.success('审核完成');
    auditModal.visible = false;
    getDataList();
  }).catch((err) => {
    message.error(err.msg || '审核失败');
  });
};

const handleDelete = (record: any) => {
  deleteApi({ids: record.order_id}).then(() => {
    message.success('删除成功');
    getDataList();
  }).catch((err) => { message.error(err.msg || '删除失败'); });
};
</script>

<style scoped lang="less">
.page-view { min-height: 100%; background: #fff; padding: 24px; display: flex; flex-direction: column; }
.table-operations { margin-bottom: 16px; display: flex; justify-content: flex-start; }
</style>