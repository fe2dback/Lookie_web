import { loadTossPayments } from '@tosspayments/payment-sdk'
const clientKey = 'test_ck_D5GePWvyJnrK0W0k6q8gLzN97Eoq'

// async/await을 사용하는 경우
async function main() {
  const tossPayments = await loadTossPayments(clientKey)
}

// Promise를 사용하는 경우
loadTossPayments(clientKey).then(tossPayments => {
  // ...
})
