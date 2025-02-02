<script setup>
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { getPeriod, getKorDateRange } from '@/utils/global.util';
import { paymentInfo, insertStaging } from '@/services/payment/payment.api';

const { VITE_TOSS_API_KEY } = import.meta.env;

const info = ref({});

const route = useRoute();
const router = useRouter();

const landId = route.query.land;
const start = route.query.start;
const end = route.query.end;
const handlePayment = ref(null);

const param = {
	land: landId,
	start,
	end,
}

const script = document.createElement('script');
onMounted(async () => {
	fetchPaymentInfo(param);
	script.src = `https://js.tosspayments.com/v2/standard`;

	script.onload = () => {
		handlePayment.value = async () => {
			try {
				const customerKey = info.value.customerKey;
				// eslint-disable-next-line no-undef
				const tossPayments = TossPayments(VITE_TOSS_API_KEY);

				const payment = tossPayments.payment({
					customerKey,
				});

				const amount = {
					currency: "KRW",
					value: getPeriod(start, end) * info.value.price,
				};

				const orderId = crypto.randomUUID();

				const tossInfo = {
					method: "CARD",
					amount,
					orderId: orderId,
					orderName: info.value.landTitle,
					successUrl: `${window.location.origin}/payment/success`,
					failUrl: `${window.location.origin}/payment/failure`,
					customerEmail: info.value.customerEmail,
					customerName: info.value.customerName,
					card: {
						useEscrow: false,
						flowMode: "DEFAULT",
						useCardPoint: false,
						useAppCardOnly: false,
					},
				};

				await staging(orderId);
				await payment.requestPayment(tossInfo);
			} catch (error) {
				console.error("결제 요청 실패:", error);
			}
		};
	};
	document.head.appendChild(script);
});

const staging = async (orderId) => {
	try {
		const data = {
			orderId: orderId,
			customerId: info.value.customerId,
			landId: Number(landId),
			start: start,
			end: end,
			amount: getPeriod(start, end) * info.value.price,
		};
		await insertStaging(data);
	} catch (err) {
		console.error(err);
	}
};

const fetchPaymentInfo = async (param) => {
	try {
		const result = await paymentInfo(param);
		info.value = result;
		console.log(result);
	} catch (err) {
		console.error(err);
		router.back()
	}
};

</script>

<template>
	<main class="flex-grow flex flex-col items-center">
		<section class="flex mt-10 px-4 w-full justify-center">
			<div class="max-w-5xl w-full border p-6 bg-white rounded-lg shadow-lg">
				<h1 class="text-2xl w-full font-bold mb-8 text-center text-gray-700">결제 페이지</h1>
				<div class="mb-8 w-full border-b pb-6">
					<h2 class="text-2xl font-semibold mb-4 text-gray-700">예약자 정보</h2>
					<div class="grid grid-cols-1 gap-6">
						<div>
							<p class="text-gray-600">예약자</p>
							<p id="user-name" class="font-semibold text-gray-800 text-lg">{{ info.customerName }}</p>
						</div>
						<div>
							<p class="text-gray-600">이메일</p>
							<p id="user-email" class="font-semibold text-gray-800 text-lg">{{ info.customerEmail }}</p>
						</div>
						<div>
							<p class="text-gray-600">전화번호</p>
							<p id="user-tel" class="font-semibold text-gray-800 text-lg">{{ info.customerTel }}</p>
						</div>
					</div>
				</div>
				<div class="mb-2 border-b pb-6">
					<h2 class="text-2xl font-semibold mb-4 text-gray-700">임대지 정보</h2>
					<div class="grid grid-cols-1 md:grid-cols-2 gap-6">
						<div>
							<p class="text-gray-600">임대지</p>
							<p id="place-name" class="font-semibold text-gray-800 text-lg">{{ info.landTitle }}</p>
						</div>
						<div>
							<p class="text-gray-600">대여 기간</p>
							<p id="rental-period" class="font-semibold flex-shrink-0 text-gray-800 text-lg">{{
								getKorDateRange(start, end) }}</p>
						</div>
						<div>
							<p class="text-gray-600">일일 금액</p>
							<p id="day-price" class="font-semibold text-gray-800 text-lg">{{
								info.price?.toLocaleString() }}원</p>
						</div>
						<div>
							<p class="text-gray-600">총 결제 금액</p>
							<p id="total-price" class="text-xl font-bold text-[#3FB8AF]">{{ (info.price *
								getPeriod(start, end)).toLocaleString() }}원</p>
						</div>
						<div class="md:col-span-2">
							<p class="text-gray-600">주소</p>
							<p id="full-address" class="font-semibold text-gray-800 text-base">{{ `[${info.zipcode}]
								${info.address}, ${info.addrDetail}` }}</p>
						</div>
					</div>
				</div>

				<div class="mb-4 pb-2 w-full">
					<span class="font-bold text-xs text-[#3FB8AF]">* 현재 카드 결제만 지원합니다.</span>
				</div>

				<div class="text-center">
					<button @click="handlePayment" id="payment-button" type="button"
						class="w-full md:w-1/3 bg-[#3FB8AF] text-white font-semibold py-3 rounded-lg hover:bg-[#2C817C] transition-colors">
						결제하기
					</button>
				</div>
			</div>
		</section>
	</main>
</template>

<style scoped></style>
