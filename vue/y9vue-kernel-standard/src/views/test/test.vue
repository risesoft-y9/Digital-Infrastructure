<!--
 * @Author: hongzhew
 * @Date: 2022-04-01 13:22:55
 * @LastEditors: hongzhew
 * @LastEditTime: 2022-04-01 13:22:55
 * @Description: 
-->

<script lang="ts" setup>
    function digui(data, targetId, moveInfo) {
        data.forEach((item) => {
            if (item.id === targetId) {
                item.push(moveInfo);
            }
            if (item.id === moveInfo.id) {
                delete item;
            }

            if (item.children) {
                digui(item.children, targetId, moveInfo);
            }
        });
    }
</script>
