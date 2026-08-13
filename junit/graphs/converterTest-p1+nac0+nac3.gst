<?xml version="1.0" encoding="UTF-8"?>
<gxl xmlns="http://www.gupro.de/GXL/gxl-1.0.dtd">
    <graph id="graph" role="graph" edgeids="false" edgemode="directed">
        <node id="n84"/>
        <node id="n85"/>
        <node id="n83"/>
        <edge from="n83" to="n83">
            <attr name="label">
                <string>use</string>
            </attr>
        </edge>
        <edge from="n84" to="n84">
            <attr name="label">
                <string>del</string>
            </attr>
        </edge>
        <edge from="n84" to="n83">
            <attr name="label">
                <string>not:c</string>
            </attr>
        </edge>
        <edge from="n85" to="n85">
            <attr name="label">
                <string>new</string>
            </attr>
        </edge>
        <edge from="n83" to="n84">
            <attr name="label">
                <string>del:a</string>
            </attr>
        </edge>
        <edge from="n83" to="n84">
            <attr name="label">
                <string>not</string>
            </attr>
        </edge>
        <edge from="n83" to="n85">
            <attr name="label">
                <string>new:b</string>
            </attr>
        </edge>
    </graph>
</gxl>
