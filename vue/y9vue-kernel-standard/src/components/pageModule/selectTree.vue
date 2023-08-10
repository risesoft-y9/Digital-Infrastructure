<template>
   <slot name="header" v-if="showHeader">
	   
		<div class="select-tree-filter-div">
			<el-button @click="onRefreshTree" class="global-btn-second" :size="fontSizeObj.buttonSize"
			:style="{ fontSize: fontSizeObj.baseFontSize }"
			>
			    <i class="ri-refresh-line"></i>
			    <span>{{ $t('刷新') }}</span>
			</el-button>
			
			<input type="password" hidden autocomplete="new-password" />
			<el-input
			    type="search"
			    :size="fontSizeObj.buttonSize"
				:style="{fontSize: fontSizeObj.baseFontSize}"
			    name="select-tree-search"
			    v-model="apiSearchKey"
			    :placeholder="$t('请搜索')"
			    autocomplete
			    @input="onSearchChange"
			>
			    <template #prefix>
			        <i class="ri-search-line"></i>
			    </template>
			</el-input>
		</div>
    </slot>
	
    <y9Tree
    ref="y9TreeRef"
	showCheckbox
	:checkStrictly="checkStrictly"
	:highlightCurrent="highlightCurrent"
    :data="alreadyLoadTreeData"
    :lazy="lazy"
    :load="onTreeLazyLoad"
    @node-click="onNodeClick"
	@node-expand="onNodeExpand"
	@check-change="onCheckChange">
    	<template #title="{item}">
    		<i :class="item.title_icon"></i>
    		<span>{{item[nodeLabel]}}</span>
    	</template>
    <!-- 	<template #actions="{item}" >
    		<i v-if="item.delete_icon" class="ri-delete-bin-7-line" @click="onRemoveNode(item)"></i>
    	</template> -->
    </y9Tree>
</template>

<script lang="ts" setup>
    import { $dataType, $deepAssignObject, $deeploneObject } from '@/utils/object'; //工具类
    import { inject, ref, useCssModule, watch } from 'vue';
	// 注入 字体对象
	const fontSizeObj: any = inject('sizeObjInfo');

    const props = defineProps({
        treeApiObj: { //tree接口对象,参数名称请严格按照以下注释进行传参。
			/**
           			{
           				topLevel:treeInterface,//可以直接返回接口，也可以返回一个函数，函数里返回tree数据。
           				childLevel:{//子级（二级及二级以上）tree接口
           					api:getTreeItemById,//可以直接返回接口，也可以返回一个函数，函数里返回tree数据，如果返回函数，params就不需要传入。
           					params:{}//请求参数，注意使用此组件，parentId字段不需要传
           				},
           				search: {//搜索接口及参数
           					api:getTreeItemById,
           					params:{}/请求参数，注意使用此组件，key字段不需要传
           				}
           			}
			*/
              type: Object,
        },

        selectField: {//设置需要选择的字段
            type: Array,
            // default: () => {
            //     return [
            //         {
            //             fieldName: 'orgType',
            //             value: ['Person', 'Position'],
            //         },
            //         {
            //             fieldName: 'disabled',
            //             value: false,
            //         },
            //     ];
            // },
        },
		
        showHeader: { //是否显示头部
            type: Boolean,
            default: true,
        },
		nodeLabel: { //显示的节点属性
			type:String,
			default:'name'
		},
		
		checkStrictly:{ //是否严格的遵循父子不互相关联的做法，默认为 false,父子互相关联
			type: Boolean,
			default:false
		},
		
		highlightCurrent: { //是否高亮当前选中节点
			type: Boolean,
			default: false,
		},
    });
	
	
	//已经加载的tree数据
	const alreadyLoadTreeData = ref([]);

    const emits = defineEmits(['onTreeClick', 'update:onCheckBox', 'change','onCheckChange','onNodeExpand']);

	//点击节点
	const onNodeClick = (node) => {
		emits('onTreeClick',node);
	}
	
	
	
	//节点被展开时触发的事件
	const onNodeExpand = (node) => {
		emits('onNodeExpand',node);
	}
	
	//当复选框被点击的时候触发
	const onCheckChange = (node,isChecked,childIsHaveChecked) => {
		emits('onCheckChange',node,isChecked,childIsHaveChecked);
		
	}
   
	
	//tree实例
	const y9TreeRef = ref();
	
	//格式化懒加载的数据
	function formatLazyTreeData(data,isTopLevel?) {
		for(let i = 0; i < data.length; i++){
				const item = data[i];
				
				if(props.selectField && props.selectField.length>0){
					//根据需要选择框的字段进行格式化
					const disabled:boolean = props.selectField.every((item2) => {//every()方法会遍历数组的每一项,如果有一项不满足条件,则返回false,剩余的项将不会再执行检测。如果遍历完数组后,每一项都符合条,则返回true。
					    if ($dataType(item2.value) == 'array') {
					        return item2.value.includes(item[item2.fieldName]);
					    } else {
					        return item[item2.fieldName] == item2.value;
					    }
					});
					
					if(!item.disabled){
						item.disabled = !disabled;//设置不可以选中的字段禁止选择
					}
					
				}
				
			    switch (item.orgType) {
					case 'Organization'://组织
						item.title_icon = 'ri-stackshare-line';
						break;
						
			        case 'Department'://部门
			            item.title_icon = 'ri-slack-line';
			            break;
			
			        case 'Group'://组
						item.isLeaf = true;//叶子节点（即没有展开按钮）
			            item.title_icon = 'ri-shield-star-line';
			            break;
			
			        case 'Position'://岗位
						item.isLeaf = true;//叶子节点（即没有展开按钮）
			            item.title_icon = 'ri-shield-user-line';
			            break;
			
			        case 'Person'://人员
						item.isLeaf = true;//叶子节点（即没有展开按钮）
			            item.title_icon = 'ri-women-line';
			            if (item.sex == 1) {
			                item.title_icon = 'ri-men-line';
			            }
			            if (item.disabled) {
			                item.name = item.name + (item.disabledRemark?item.disabledRemark:"[禁用]");
			            }
			            break;
			
			        default:
			            item.title_icon = '';
			    }
			    // 资源
			    switch (item.resourceType) {
			
			        case 0://应用
			            item.title_icon = 'ri-apps-line';
			            break;
			
			        case 1://菜单
			            item.title_icon = 'ri-menu-4-line';
			            break;
			
			        case 2://按钮
			            item.title_icon = 'ri-checkbox-multiple-blank-line';
			            break;
			
			    }
			
			    // 角色
			    switch (item.type) {
			
			        case 'role'://角色 人员
			            item.title_icon = 'ri-contacts-line';
			            break;
			
			        case 'folder': // 文件夹
			            item.title_icon = 'ri-folder-2-line';
			            break;
			    }
		}
	}
	
	//懒加载
	const onTreeLazyLoad = async (node, resolve: () => void) => {
	
		if (node.$level === 0) {
			
			//1.获取数据
			let data = [];
			const res = await props.treeApiObj?.topLevel();//请求一级节点接口
			data = res.data || res;
			
			
			//2.格式化数据
			await formatLazyTreeData(data,true)
			
			
			return resolve(data)//返回一级节点数据
				
	
		} else {
			
			//1.获取数据
			let data = [];
			if(node.children && node.children.length > 0){//如果有传入的数据就取传入的数据
				data = node.children;
			}else{//如果没有则取接口数据
				//整合参数
				let params = {}
				const childLevelParams = props.treeApiObj?.childLevel?.params
				if(childLevelParams){
					params = childLevelParams;
				}
				params.parentId = node.id;
				//请求接口
				const res = await props.treeApiObj?.childLevel?.api(params);
				data = res.data || res;
			}
			
			//2.格式化数据
			await formatLazyTreeData(data,false)
			
			
			return resolve(data);//返回二级三级...节点数据
		}
	}
	
    
   
    
    //搜索关键字
    const apiSearchKey = ref('');
	
	let timer;
	const onSearchChange = (searchKey) => {
		  clearTimeout(timer); //清空计时器
		  timer = setTimeout(() => {
			  postSearchApi(searchKey)
		  },500)
	}
	
	watch(
		() => props.treeApiObj?.search?.params?.key,
		(searchKey) => {
			postSearchApi(searchKey)
		},
	)
	
	//请求搜索api
	async function postSearchApi(searchkey){
		if(searchkey){//有值就请求搜索api
				//整合参数
				let params = {}
				const searchParams = props.treeApiObj?.search?.params
				if(searchParams){
					params = searchParams;
				}
				params.key = searchkey;
				
				//请求搜索接口
				const res = await props.treeApiObj?.search?.api(params)
				const data = res.data;
				//格式化tree数据
				await formatLazyTreeData(data)
				// resourceType 为0 的parentId 为空
				await data?.map(item => {
					if(item.resourceType  == 0) {
						item.parentId = '';
					}
				})
				//根据搜索结果转换成tree结构显示出来
				alreadyLoadTreeData.value = transformTreeBySearchResult(data);
			
				nextTick(() => {
					y9TreeRef.value.setExpandAll()
				})
				
			
				
			}else{//没有就获取获取已经懒加载过的数据，并且设置默认选中第一个节点、默认展开第一个节点，模拟点击第一个节点
				
				alreadyLoadTreeData.value = y9TreeRef.value.getLazyTreeData();//获取已经懒加载过的数据
				
				nextTick(() => {
					if(alreadyLoadTreeData.value.length > 0){
						y9TreeRef.value.setCurrentKey(alreadyLoadTreeData.value[0].id);//设置第一个节点为高亮节点
						
						y9TreeRef.value.setExpandKeys([alreadyLoadTreeData.value[0].id])//设置第一个节点展开
						
						onNodeClick(alreadyLoadTreeData.value[0]);//模拟点击第一个节点
					}
				})
				
			
			}
		
	}
	
	
	//根据搜索结果转换成tree结构
	function transformTreeBySearchResult(result){
			const treeData = [];
			for(let i = 0; i<result.length; i++){
				const item = result[i];
				if(!item.parentId){//一级节点
					let node = item;
					
					const child = result.filter(resultItem => resultItem.parentId === item.id);
					if(child.length > 0){//如果有子节点则递归子节点，进行组合
						item.children = child;
						const fn2 = (data) => {
							for(let j = 0; j<data.length; j++){
								const itemJ = data[j];
								const childs = result.filter(resultItem => resultItem.parentId === itemJ.id);
								if(childs.length > 0){
									itemJ.children = childs
									if(itemJ.children.length > 0){
										fn2(itemJ.children)
									}
								}
							}
						}
						fn2(item.children);//递归子节点
					}
					
					
					treeData.push(item)
					
					
				}
			}
			
			return treeData
	}
	
	//是否懒加载
	const lazy:boolean = ref(true);
	
	//刷新tree
	const onRefreshTree = () => {
		alreadyLoadTreeData.value = [];
		lazy.value = false;
		setTimeout(() => {
			lazy.value = true;
		},0)
	}
	
	/**根据id查找tree节点信息
	 * @param {Object} data 树的数据
	 * @param {Object} targetId 目标节点id
	 */
	function findNode(data,targetId){
		let currNodeInfo = null;;
		const fun = (data,targetId) => {
			for(let i = 0; i<data.length; i++){
				const item = data[i];
				if(item.id === targetId){
					currNodeInfo = item;
					break;
				}
				if(item.children && item.children.length > 0){
					fun(item.children,targetId);
				}
			}
		}
		fun(data,targetId);
		return currNodeInfo;
	}
	
	//设置树数据
	async function setTreeData(data){
		await formatLazyTreeData(data);
		alreadyLoadTreeData.value = data;
	}
	
	//获取树数据
	function getTreeData(){
		return alreadyLoadTreeData.value 
	}

    defineExpose({
		y9TreeRef,
        onRefreshTree, //刷新tree
        getTreeData,
    });
</script>

<style lang="scss" scoped>
    @import '@/theme/global.scss';
    @import '@/theme/global-vars.scss';
    @import '@/theme/global.scss';
	

    //过滤样式
    .select-tree-filter-div {
      display: flex;
	  align-items: center;
	  flex-wrap: wrap;
	   margin-bottom: 16px;
      .el-button {
          margin-right: 16px;
		   margin-bottom: 10px;
      }
	  :deep(.el-input){
		  width: 250px;
		  max-width:100%;
		    margin-bottom: 10px;
		  .el-input__wrapper{
			  border-radius: 30px;
			  box-shadow: 0 2px 4px 0 rgb(0 0 0 / 5%);
			  border:1px solid var(--el-color-primary-light-7);
		  }
		 
		
	  }
    }
	
	:deep(.node-title){
		display: inline-flex;
		align-items: center;
		i{
			margin-right: 5px;
			font-weight: normal;
			
		}
	}
	:deep(.active-node){
		.y9-tree-item-content{
			.y9-tree-item-content-div{
				.action-icons{
					i{
						color: var(--el-color-white) !important;
					}
					
				}
			}
		}
		
	}
</style>
