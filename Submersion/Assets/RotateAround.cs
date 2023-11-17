using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class RotateAround : MonoBehaviour
{
    public float speed;
    public float divider;
    public GameObject pivot;
    float timer;
    // Start is called before the first frame update
    void Start()
    {
        timer = 0;
    }

    // Update is called once per frame
    void Update()
    {
        timer += Time.deltaTime;
        transform.RotateAround(pivot.transform.position, new Vector3(0, -1, 0), speed * Time.deltaTime);
        transform.Translate(Vector2.up * divider*(Mathf.Cos(timer * (speed/50) / Mathf.PI)));
    }
}
