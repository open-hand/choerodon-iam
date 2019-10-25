export default function (status) {
  switch (status) {
    case 'unExpired':
    case 'enabled':
    case 'reconnection':
    case 'published':
    case 'available':
    case 'completed':
    case 'success':
    case 'released':
      return '#00bfa5';
    case 'disconnect':
    case 'disabled':
    case 'expired':
    case 'rejected':
    case 'failed':
      return '#f44336';
    case 'unpublished':
    case 'break':
      return '#ccc';
    case 'revert':
    case 'withdrawn':
      return '#ff7043';
    case 'unconfirmed':
      return '#ffb100';
    case 'under_approval':
    case 'publishing':
    case 'downloading':
      return '#4d90fe';
    // 容错机制
    default:
      return '#000';
  }
}
