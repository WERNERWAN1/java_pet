<template>
  <div>
    <!--页面区域-->
    <div class="page-view">
      <div class="table-operations">
        <a-space>
<!--          <a-button type="primary" @click="handleAdd">模拟新增</a-button>-->
          <a-button @click="handleBatchDelete">批量删除</a-button>
        </a-space>
      </div>
      <a-table
        size="middle"
        rowKey="id"
        :loading="data.loading"
        :columns="columns"
        :data-source="data.tagList"
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
          <template v-if="column.key === 'cover'">
            <a-image
              v-if="record.cover"
              :src="getImgUrl(record.cover)"
              :width="48"
              :height="48"
              style="border-radius: 4px; object-fit: cover;"
            />
            <span v-else>--</span>
          </template>
          <template v-if="column.key === 'orderType'">
            <a-tag :color="Number(text) === 2 ? '#87d068' : '#2db7f5'">
              {{ getOrderTypeText(text) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'status'">
            <a-tag :color="getStatusColor(text)">
              {{ getStatusText(text) }}
            </a-tag>
          </template>
          <template v-if="column.key === 'operation'">
            <span>
              <a @click="openDetail(record)">详情</a>
              <a-divider type="vertical" />
              <a @click="openEdit(record)">修改</a>
              <a-divider type="vertical" />
              <template v-if="Number(record.status) === 1">
                <a-popconfirm title="确认该订单已支付?" ok-text="是" cancel-text="否" @confirm="confirmPay(record)">
                  <a>标记支付</a>
                </a-popconfirm>
                <a-divider type="vertical" />
              </template>
              <a-popconfirm title="确定取消?" ok-text="是" cancel-text="否" @confirm="confirmCancel(record)">
                <a>取消</a>
              </a-popconfirm>
              <a-divider type="vertical" />
              <a-popconfirm title="确定删除?" ok-text="是" cancel-text="否" @confirm="confirmDelete(record)">
                <a>删除</a>
              </a-popconfirm>
            </span>
          </template>
        </template>
      </a-table>

      <a-modal
        :visible="detail.visible"
        title="订单详情"
        :footer="null"
        width="760px"
        @cancel="detail.visible = false"
      >
        <a-descriptions bordered :column="2" size="small">
          <a-descriptions-item label="订单编号">{{ detail.record.orderNumber || '--' }}</a-descriptions-item>
          <a-descriptions-item label="订单状态">
            <a-tag :color="getStatusColor(detail.record.status)">{{ getStatusText(detail.record.status) }}</a-tag>
          </a-descriptions-item>
          <a-descriptions-item label="类别">{{ getOrderTypeText(detail.record.orderType) }}</a-descriptions-item>
          <a-descriptions-item label="下单用户">{{ detail.record.username || '--' }}</a-descriptions-item>
          <a-descriptions-item label="购买商品&宠物" :span="2">
            <div style="display: flex; align-items: center; gap: 12px;">
              <a-image
                v-if="detail.record.cover"
                :src="getImgUrl(detail.record.cover)"
                :width="64"
                :height="64"
                style="border-radius: 4px; object-fit: cover;"
              />
              <span>{{ detail.record.title || '--' }}</span>
            </div>
          </a-descriptions-item>
          <a-descriptions-item label="成交单价">¥{{ formatMoney(detail.record.unitPrice) }}</a-descriptions-item>
          <a-descriptions-item label="购买数量">{{ detail.record.count ?? '--' }}</a-descriptions-item>
          <a-descriptions-item label="商品总额">¥{{ formatMoney(goodsTotal(detail.record)) }}</a-descriptions-item>
          <a-descriptions-item label="优惠金额">-¥{{ formatMoney(detail.record.discountAmount) }}</a-descriptions-item>
          <a-descriptions-item label="实付金额">
            <span style="color: #ff4d4f; font-weight: 600;">¥{{ formatMoney(detail.record.amount) }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="获得积分">
            <span>{{ Number(detail.record.awardScore) > 0 ? `+${detail.record.awardScore}` : '未发放' }}</span>
          </a-descriptions-item>
          <a-descriptions-item label="订单时间">{{ getFormatTime(detail.record.orderTime, true) || '--' }}</a-descriptions-item>
          <a-descriptions-item label="支付时间">{{ getFormatTime(detail.record.payTime, true) || '--' }}</a-descriptions-item>
          <a-descriptions-item label="收货人">{{ detail.record.receiverName || '--' }}</a-descriptions-item>
          <a-descriptions-item label="联系电话">{{ detail.record.receiverPhone || '--' }}</a-descriptions-item>
          <a-descriptions-item label="收货地址" :span="2">{{ detail.record.receiverAddress || '--' }}</a-descriptions-item>
          <a-descriptions-item label="买家备注" :span="2">{{ detail.record.remark || '--' }}</a-descriptions-item>
          <a-descriptions-item label="创建人">{{ detail.record.createByName || '--' }}</a-descriptions-item>
          <a-descriptions-item label="创建时间">{{ getFormatTime(detail.record.createTime, true) || '--' }}</a-descriptions-item>
        </a-descriptions>
      </a-modal>

      <a-modal
        :visible="edit.visible"
        title="修改订单"
        ok-text="保存"
        cancel-text="取消"
        width="600px"
        @cancel="edit.visible = false"
        @ok="submitEdit"
      >
        <a-form :label-col="{ style: { width: '100px' } }">
          <a-form-item label="商品">
            <span>{{ edit.record.title || '--' }}</span>
          </a-form-item>
          <a-form-item label="成交单价">
            <span>¥{{ formatMoney(edit.record.unitPrice) }}</span>
          </a-form-item>
          <a-form-item label="购买数量">
            <a-input-number v-model:value="edit.form.count" :min="1" :precision="0" style="width: 100%;" />
          </a-form-item>
          <a-form-item label="优惠金额">
            <a-input-number v-model:value="edit.form.discountAmount" :min="0" :precision="2" style="width: 100%;" />
          </a-form-item>
          <a-form-item label="商品总额">
            <span>¥{{ formatMoney(editTotal) }}</span>
          </a-form-item>
          <a-form-item label="实付金额">
            <span style="color: #ff4d4f; font-weight: 600;">¥{{ formatMoney(editPayAmount) }}</span>
          </a-form-item>
          <a-form-item label="联系电话">
            <a-input v-model:value="edit.form.receiverPhone" placeholder="请输入联系电话" />
          </a-form-item>
          <a-form-item label="收货地址">
            <a-textarea v-model:value="edit.form.receiverAddress" :rows="2" placeholder="请输入收货地址" />
          </a-form-item>
        </a-form>
      </a-modal>
    </div>

  </div>
</template>

<script setup lang="ts">
  import { FormInstance, message } from 'ant-design-vue';
  import { createApi, listApi, updateApi, deleteApi, cancelApi, payApi } from '/@/api/order';
  import {getFormatTime} from "/@/utils";
  import {BASE_URL} from "/@/store/constants";

  const getImgUrl = (img: string) => BASE_URL + '/api/staticfiles/image/' + img;

  const getStatusText = (s: any) => ({'1': '待支付', '2': '已支付', '3': '已完成', '7': '已取消'}[String(s)] || '未知');
  const getStatusColor = (s: any) => ({'1': 'orange', '2': 'blue', '3': 'green', '7': 'red'}[String(s)] || 'default');
  const getOrderTypeText = (t: any) => Number(t) === 2 ? '商城用品' : '宠物商品';
  const formatMoney = (v: any) => Number(v ?? 0).toFixed(2);

  const detail = reactive({
    visible: false,
    record: {} as any,
  });

  // 编辑弹窗：支持调整购买数量、优惠金额、联系电话与收货地址
  const edit = reactive({
    visible: false,
    record: {} as any,
    form: {count: undefined as any, discountAmount: 0, receiverPhone: '', receiverAddress: ''},
  });

  const goodsTotal = (record: any) => Number(record?.unitPrice ?? 0) * Number(record?.count ?? 0);
  const editTotal = computed(() => Number(edit.record.unitPrice ?? 0) * Number(edit.form.count ?? 0));
  const editPayAmount = computed(() => Math.max(editTotal.value - Number(edit.form.discountAmount ?? 0), 0));

  const openEdit = (record: any) => {
    if (Number(record.status) === 7) {
      message.warn('订单已取消，不可修改');
      return;
    }
    edit.record = record;
    edit.form.count = record.count ?? 1;
    edit.form.discountAmount = Number(record.discountAmount ?? 0);
    edit.form.receiverPhone = record.receiverPhone || '';
    edit.form.receiverAddress = record.receiverAddress || '';
    edit.visible = true;
  };

  const submitEdit = () => {
    if (!edit.form.count || edit.form.count <= 0) {
      message.warn('购买数量必须大于 0');
      return;
    }
    const discount = Number(edit.form.discountAmount ?? 0);
    if (discount < 0) {
      message.warn('优惠金额不能为负数');
      return;
    }
    if (discount > editTotal.value) {
      message.warn('优惠金额不能超过商品总额');
      return;
    }
    const formData = new FormData();
    formData.append('id', edit.record.id);
    formData.append('count', edit.form.count);
    formData.append('discountAmount', discount);
    formData.append('receiverPhone', edit.form.receiverPhone || '');
    formData.append('receiverAddress', edit.form.receiverAddress || '');
    updateApi({}, formData).then(() => {
      message.success('修改成功');
      edit.visible = false;
      getDataList();
    }).catch((err) => { message.error(err.msg || '修改失败'); });
  };

  const openDetail = (record: any) => {
    detail.record = record;
    detail.visible = true;
  };


  const columns = reactive([
    {
      title: '序号',
      dataIndex: 'index',
      key: 'index',
      align: 'center'
    },
    {
      title: '订单编号',
      dataIndex: 'orderNumber',
      key: 'orderNumber',
      align: 'center',
      width: 120
    },
    {
      title: '用户',
      dataIndex: 'username',
      key: 'username',
      align: 'center'
    },
    {
      title: '图片',
      dataIndex: 'cover',
      key: 'cover',
      align: 'center',
      width: 80
    },
    {
      title: '购买商品&宠物',
      dataIndex: 'title',
      key: 'title',
      align: 'center',
      customRender: ({text}) => text ? text.substring(0, 10) + '...' : '--'
    },
    {
      title: '类别',
      dataIndex: 'orderType',
      key: 'orderType',
      align: 'center',
      width: 110
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      align: 'center',
      scopedSlots: {customRender: 'status'}
    },
    {
      title: '成交金额',
      dataIndex: 'amount',
      key: 'amount',
      align: 'center',
      customRender: ({text}) => `¥${formatMoney(text)}`
    },
    {
      title: '订单时间',
      dataIndex: 'orderTime',
      key: 'orderTime',
      align: 'center',
      customRender: ({text}) => getFormatTime(text, true)
    },
    {
      title: '创建人',
      dataIndex: 'createByName',
      key: 'createByName',
      align: 'center',
      width: 100,
      customRender: ({text}) => text || '--'
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      key: 'createTime',
      align: 'center',
      width: 170,
      customRender: ({text}) => getFormatTime(text, true) || '--'
    },
    {
      title: '修改人',
      dataIndex: 'updateByName',
      key: 'updateByName',
      align: 'center',
      width: 100,
      customRender: ({text}) => text || '--'
    },
    {
      title: '修改时间',
      dataIndex: 'updateTime',
      key: 'updateTime',
      align: 'center',
      width: 170,
      customRender: ({text}) => getFormatTime(text, true) || '--'
    },
    {
      title: '操作',
      dataIndex: 'action',
      key: 'operation',
      align: 'center',
      fixed: 'right',
      width: 220,
    },
  ]);

  // 页面数据
  const data = reactive({
    tagList: [],
    loading: false,
    keyword: '',
    selectedRowKeys: [] as any[],
    pageSize: 10,
    page: 1,
  });


  onMounted(() => {
    getDataList();
  });

  const getDataList = () => {
    data.loading = true;
    listApi({
      keyword: data.keyword,
    })
      .then((res) => {
        data.loading = false;
        console.log(res);
        res.data.forEach((item: any, index: any) => {
          item.index = index + 1;
        });
        data.tagList = res.data;
      })
      .catch((err) => {
        data.loading = false;
        console.log(err);
      });
  };


  const rowSelection = ref({
    onChange: (selectedRowKeys: (string | number)[], selectedRows: DataItem[]) => {
      console.log(`selectedRowKeys: ${selectedRowKeys}`, 'selectedRows: ', selectedRows);
      data.selectedRowKeys = selectedRowKeys;
    },
  });

  const confirmPay = (record: any) => {
    payApi({id: record.id}).then(() => {
      message.success('已标记为已支付');
      getDataList();
    }).catch((err) => { message.error(err.msg || '操作失败'); });
  };

  const confirmCancel = (record: any) => {
    cancelApi({ id: record.id })
        .then((res) => {
          getDataList();
          message.success('取消成功')
        })
        .catch((err) => {
          message.error(err.msg || '操作失败');
        });
  };

  const confirmDelete = (record: any) => {
    console.log('delete', record);
    deleteApi({ ids: record.id })
      .then((res) => {
        getDataList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
  };

  const handleAdd = () => {
    // createApi({
    //   thingId: 1,
    //   userId: 2,
    //   count: 1
    // }).then(res => {
    //   getDataList()
    // }).catch(err => {
    //
    // })
  }

  const handleBatchDelete = () => {
    console.log(data.selectedRowKeys);
    if (data.selectedRowKeys.length <= 0) {
      console.log('hello');
      message.warn('请勾选删除项');
      return;
    }
    deleteApi({ ids: data.selectedRowKeys.join(',') })
      .then((res) => {
        message.success('删除成功');
        data.selectedRowKeys = [];
        getDataList();
      })
      .catch((err) => {
        message.error(err.msg || '操作失败');
      });
  };

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
    gap: 8px;
  }
</style>
