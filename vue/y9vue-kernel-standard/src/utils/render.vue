

<script lang="ts">
	export default {
	    name: 'renderComponent',
	    functional: true,
	    props: {
	        render: Function,
			data: Object,
	    },
	    render(h, ctx){
			if(h.data){
				return h.render(h.data.row,h.data);
			}else {
				return h.render(h)
			}
	    }
	};
</script>