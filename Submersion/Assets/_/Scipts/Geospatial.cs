using Google.XR.ARCoreExtensions;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public class Geospatial : MonoBehaviour
{
    FeatureSupported isGeoCompatible;

    private AREarthManager earthManager;

    private void Awake()
    {
        earthManager = GetComponent<AREarthManager>();
        isGeoCompatible = earthManager.IsGeospatialModeSupported(GeospatialMode.Enabled);
    }

    // Start is called before the first frame update
    void Start()
    {
        
    }
   
    // Update is called once per frame
    void Update()
    {
        
    }
}
