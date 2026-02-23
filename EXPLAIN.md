# Пример поискового запроса

```sql
SELECT * FROM document
WHERE sstatus = 'DRAFT'
ORDER BY created_at DESC
LIMIT 20;
```

## Анализ запроса

```
Limit  (cost=6744.72..6747.06 rows=20 width=66) (actual time=79.167..80.937 rows=20 loops=1)
   ->  Gather Merge  (cost=6744.72..20414.34 rows=117160 width=66) (actual time=79.165..80.934 rows=20 loops=1)
         Workers Planned: 2
         Workers Launched: 2
         ->  Sort  (cost=5744.70..5891.15 rows=58580 width=66) (actual time=73.625..73.626 rows=16 loops=3)
               Sort Key: created_at DESC
               Sort Method: top-N heapsort  Memory: 28kB
               Worker 0:  Sort Method: top-N heapsort  Memory: 30kB
               Worker 1:  Sort Method: top-N heapsort  Memory: 30kB
               ->  Parallel Seq Scan on document  (cost=0.00..4185.91 rows=58580 width=66) (actual time=5.232..70.133 rows=46562 loops=3)
                     Filter: ((status)::text = 'DRAFT'::text)
                     Rows Removed by Filter: 21272
 Planning Time: 7.100 ms
 Execution Time: 81.654 ms
(14 rows)
```

## Добавляем индекс

```sql
CREATE INDEX idx_document_status_created ON document (status, created_at DESC);
```

## Анализ после добавления индекса

```
Limit  (cost=0.42..2.65 rows=20 width=66) (actual time=0.027..0.047 rows=20 loops=1)
   ->  Index Scan using idx_document_status_created on document  (cost=0.42..15701.85 rows=140593 width=66) (actual time=0.026..0.044 rows=20 loops=1)
         Index Cond: ((status)::text = 'DRAFT'::text)
 Planning Time: 3.575 ms
 Execution Time: 0.068 ms
(5 rows)
```

# Итого
После добавления индекса Execution Time сократилось с 81.654 ms до 0.068 ms, а Planning Time с 7.100 ms до 3.575 ms
