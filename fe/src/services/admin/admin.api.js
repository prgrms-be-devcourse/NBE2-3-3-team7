import { jsonRequest } from '@/services/api';

export const adminDashboard = async () => {
	const response = await jsonRequest('/admin/dashboard', 'GET');

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
};

export const adminReceipts = async (param) => {
	const query = param
		? '?' + new URLSearchParams(param).toString()
		: '';

	const response = await jsonRequest(`/admin/receipts${query}`, 'GET')

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
}

export const adminPopups = async (param) => {
	const query = param
		? '?' + new URLSearchParams(param).toString()
		: '';

	const response = await jsonRequest(`/admin/popups${query}`, 'GET')

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
}

export const adminLands = async (param) => {
	const query = param
		? '?' + new URLSearchParams(param).toString()
		: '';

	const response = await jsonRequest(`/admin/lands${query}`, 'GET')

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
}

export const dailyAnalytics = async (param) => {
	const query = param
		? '?' + new URLSearchParams(param).toString()
		: '';

	const response = await jsonRequest(`/admin/analytics/daily${query}`, 'GET')

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
}

export const monthlyAnalytics = async (param) => {
	const query = param
		? '?' + new URLSearchParams(param).toString()
		: '';

	const response = await jsonRequest(`/admin/analytics/monthly${query}`, 'GET')

	if (!response || !response.data) {
		throw new Error('API 응답 데이터가 유효하지 않습니다.');
	}

	return response.data;
}