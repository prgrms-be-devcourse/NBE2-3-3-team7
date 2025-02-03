<script setup>
import { onMounted, ref, watch } from 'vue';
import { useRoute } from 'vue-router';
import ReservationItem from "@/components/user/ReservationItem.vue";
import BasePaging from '@/components/common/BasePaging.vue';
import NoItem from '@/components/common/NoItem.vue';
import RouterBack from '@/components/common/RouterBack.vue';
import { reservationByLand } from '@/services/user/user.payment';

const route = useRoute();

const reservation = ref({ content: [], page: { totalPages: 0, number: 0 } });
const landTitle = ref("");

onMounted(async () => {
	await fetchReservation()
})

watch(
	() => route.query,
	() => {
		fetchReservation();
	},
	{ deep: true }
);

const fetchReservation = async () => {
	try {
		const result = await reservationByLand(route.params.id, route.query);
		reservation.value = result.reservation;
		landTitle.value = result.landTitle
	} catch (err) {
		console.error('API 요청 오류:', err);
	}
};

</script>

<!-- /user/land/:id/reservation -->
<template>
	<main class="flex-grow flex flex-col items-center relative">
		<RouterBack link="/user/land" text="임대지 예약 내역 목록" >
			<h3 class="text-base font-semibold text-right flex-grow">{{ landTitle }}</h3>
		</RouterBack>
		<section class="mt-4 w-full px-4">
			<div class="max-w-4xl mx-auto my-6">
				<div v-if="reservation.content.length > 0" class="grid grid-cols-1 gap-5">
					<ReservationItem v-for="(item, index) in reservation.content" :key="index" :item="item" />
				</div>
				<NoItem v-else message="예약 내역이 존재하지 않습니다." />
			</div>
		</section>
		<section class="flex py-4 max-w-5xl w-full border-b border-gray-300 justify-center px-4"
			aria-label="pagination">
			<div v-if="reservation.page.totalPages > 0"
				class="flex items-center flex-col space-y-2 justify-center bg-white px-4 py-3">
				<BasePaging :totalPages="reservation.page.totalPages" :currentPage="reservation.page.number" url="/user/land" />
			</div>
		</section>
	</main>
</template>