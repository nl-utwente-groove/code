<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n86"/>
        <node id="n87"/>
        <node id="n88"/>
        <edge from="n86" to="n86">
            <attr name="label">
                <string>use</string>
            </attr>
        </edge>
        <edge from="n87" to="n87">
            <attr name="label">
                <string>del</string>
            </attr>
        </edge>
        <edge from="n86" to="n87">
            <attr name="label">
                <string>not</string>
            </attr>
        </edge>
        <edge from="n86" to="n88">
            <attr name="label">
                <string>new:b</string>
            </attr>
        </edge>
        <edge from="n88" to="n88">
            <attr name="label">
                <string>new</string>
            </attr>
        </edge>
        <edge from="n86" to="n87">
            <attr name="label">
                <string>del:a</string>
            </attr>
        </edge>
    </graph>
</gxl>
