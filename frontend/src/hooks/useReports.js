import { useState, useEffect } from 'react';
import api from '../utilities/ApiUtils';

export function useReports(type = 'summary') {
  const [data,    setData]    = useState(null);
  const [loading, setLoading] = useState(false);
  const [error,   setError]   = useState(null);

  useEffect(() => {
    setLoading(true); setError(null);
    api.get(`/api/reports/${type}`)
      .then(setData)
      .catch(e => setError(e.message))
      .finally(() => setLoading(false));
  }, [type]);

  return { data, loading, error };
}
