import { defineStore } from 'pinia';
import i18n from '@/language/index';
import { deleteIcon, getAppIconPageList, saveIcon, searchIconPageByName, uploadIcon } from '@/api/appIcon/index';
import { forIn } from 'lodash';

const { t } = i18n.global;

export const useIconStore = defineStore('iconStore', {
    state: () => {
        return {
            total: 0,
            totalPages: 0,
            currPage: 1,
            searchKey: '',
            iconList: [],
            searchCateValue: '',
            searchCate: [
                {
                    label: '全部',
                    value: '0'
                }
            ]
        };
    },
    getters: {
        getTotal: (state) => {
            return state.total;
        },
        getSearchKey: (state) => {
            return state.searchKey;
        },
        getIconList: (state) => {
            if (state.searchKey) {
                let searchList = [];
                state.iconList.map((item) => {
                    let children = item.children.filter((child) => {
                        return child.name.indexOf(state.searchKey) > -1;
                    });
                    let obj = {};
                    for (let key in item) {
                        obj[key] = item[key];
                    }
                    if (children.length) {
                        obj.children = children;
                        searchList.push(obj);
                    }
                });
                if (state.searchCateValue && state.searchCateValue != '0') {
                    let cate = state.searchCate.filter((item) => item.value == state.searchCateValue);
                    console.log(cate[0]);
                    return searchList.filter((item) => item.cateName == cate[0].label);
                } else {
                    return searchList;
                }
                return searchList;
            } else {
                return state.iconList;
            }
        },
        getSearchCateValue: (state) => {
            return state.searchCateValue;
        },
        getSearchCate: (state) => {
            return state.searchCate;
        }
    },

    actions: {
        // 获取所有图标
        async setIconList(status = 'all') {
            try {
                let iconData = await getAppIconPageList({ page: 1, rows: 1000 });
                this.total = iconData.total;
                this.totalPages = iconData.totalPages;
                this.currPage = iconData.currPage;
                let rows = iconData.rows;
                let count = 0;
                // 按照分类属性进行分类
                const cateByCategory = (item) => {
                    let hasCateNameByCategory = false;
                    item.iconData = 'data:image/png;base64,' + item.iconData;
                    item.id = `${item.id}-${count++}`;
                    item.showMask = false;
                    this.iconList.map((it) => {
                        if (it.cateName == item.category) {
                            hasCateNameByCategory = true;
                            it.children.push(item);
                        }
                    });
                    // 添加该分类属性
                    if (!hasCateNameByCategory) {
                        this.iconList.push({
                            id: this.iconList.length + '',
                            cateName: item.category,
                            children: [item]
                        });
                        this.searchCate.push({
                            label: item.category,
                            value: this.iconList.length + ''
                        });
                    }
                };
                // 按照颜色分类
                const cateByColorType = (item) => {
                    let hasCateNameByColorType = false;
                    item.iconData = 'data:image/png;base64,' + item.iconData;
                    item.id = `${item.id}-${count++}`;
                    item.showMask = false;
                    this.iconList.map((it) => {
                        if (it.cateName == item.colorType) {
                            hasCateNameByColorType = true;
                            it.children.push(item);
                        }
                    });
                    // 添加该分类属性
                    if (!hasCateNameByColorType) {
                        this.iconList.push({
                            id: this.iconList.length + '',
                            cateName: item.colorType,
                            children: [item]
                        });
                        this.searchCate.push({
                            label: item.colorType,
                            value: this.iconList.length + ''
                        });
                    }
                };
                // 按照其他属性分类
                const cateByOther = (item) => {
                    let hasCateNameByOther = false;
                    item.iconData = 'data:image/png;base64,' + item.iconData;
                    item.id = `${item.id}-${count++}`;
                    item.showMask = false;
                    this.iconList.map((it) => {
                        if (it.cateName == 'other') {
                            hasCateNameByOther = true;
                            it.children.push(item);
                        }
                    });
                    // 添加该分类属性
                    if (!hasCateNameByOther) {
                        this.iconList.push({
                            id: this.iconList.length + '',
                            cateName: 'other',
                            children: [item]
                        });
                        this.searchCate.push({
                            label: 'other',
                            value: this.iconList.length + ''
                        });
                    }
                };
                while (this.iconList.length) {
                    this.iconList.pop();
                }
                for (let i = 0; i < rows.length; i++) {
                    let item = rows[i];
                    if (item.category) {
                        cateByCategory(item);
                        cateByColorType(item);
                    } else if (item.colorType) {
                        cateByColorType(item);
                    } else {
                        cateByOther(item);
                    }
                }
            } catch (error) {
                console.log(error);
            }
        },
        clearSearchKey() {
            this.searchKey = '';
            this.searchCateValue = '0';
        },
        searchByNodeId(node, status = 'enter') {
            this.iconList.map((cate) => {
                cate.children.map((item, i) => {
                    if (item.id == node.id) {
                        switch (status) {
                            case 'enter':
                                item.showMask = true;
                                break;
                            case 'leave':
                                item.showMask = false;
                                break;
                            case 'delete':
                                cate.children.splice(i, 1);
                                break;
                            case 'save':
                                item.name = node.name;
                                item.remark = node.remark;
                                break;
                            default:
                                break;
                        }
                        // return item;
                    }
                });
            });
        },
        async deleteIconById(node) {
            let that = this;
            try {
                /**
                 * 新图标的id因为可能存在多个分类，所以对id进行了修改，所以这里取的时候，和后端交互时，需要取原先的id
                 * 兼容老图标的id
                 */
                // console.log(node);
                let id = node.id.split('-')[0];
                return deleteIcon(id)
                    .then((res) => {
                        if (res.success) {
                            that.searchByNodeId(node, 'delete');
                        }
                        return res;
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            } catch (error) {}
        },
        async saveIconById(node) {
            let that = this;
            try {
                return saveIcon({ name: node.name, remark: node.remark, id: node.id.split('-')[0] })
                    .then((res) => {
                        if (res.success) {
                            that.searchByNodeId(node, 'save');
                        }
                        return res;
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            } catch (error) {
                console.log(error);
            }
        },
        async saveIconByUpload(fromatData) {
            let that = this;
            try {
                return await uploadIcon(fromatData)
                    .then(async (res) => {
                        if (res.success) {
                            await that.setIconList('save');
                        }
                        return res;
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            } catch (error) {
                console.log(error);
            }
        },
        initData() {
            this.clearSearchKey();
            let countTotal = false;
            if (!this.total) {
                countTotal = true;
            }
            if (this.iconList.length) {
                this.iconList.map((cate) => {
                    cate.children.map((item, i) => {
                        if (item.showMask) {
                            item.showMask = false;
                        }
                        if (countTotal) {
                            this.total += 1;
                        }
                    });
                });
            }
        }
    }
});
